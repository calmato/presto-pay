package service

import (
	"context"
	"time"

	"github.com/calmato/presto-pay/api/calc/internal/domain"
	"github.com/calmato/presto-pay/api/calc/internal/domain/group"
	"github.com/calmato/presto-pay/api/calc/internal/domain/payment"
	"github.com/calmato/presto-pay/api/calc/internal/domain/user"
	"github.com/calmato/presto-pay/api/calc/internal/infrastructure/api"
	"github.com/calmato/presto-pay/api/calc/internal/infrastructure/notification"
	"github.com/google/uuid"
	"golang.org/x/xerrors"
)

type paymentService struct {
	paymentDomainValidation payment.PaymentDomainValidation
	paymentRepository       payment.PaymentRepository
	paymentUploader         payment.PaymentUploader
	groupRepository         group.GroupRepository
	apiClient               api.APIClient
	notificationClient      notification.NotificationClient
}

// NewPaymentService - PaymentServiceの生成
func NewPaymentService(
	pdv payment.PaymentDomainValidation, pr payment.PaymentRepository, pu payment.PaymentUploader,
	gr group.GroupRepository, ac api.APIClient, nc notification.NotificationClient,
) payment.PaymentService {
	return &paymentService{
		paymentDomainValidation: pdv,
		paymentRepository:       pr,
		paymentUploader:         pu,
		groupRepository:         gr,
		apiClient:               ac,
		notificationClient:      nc,
	}
}

func (ps *paymentService) Index(ctx context.Context, groupID string, startAt string) ([]*payment.Payment, error) {
	// グループ情報取得
	g, err := ps.groupRepository.Show(ctx, groupID)
	if err != nil {
		return nil, domain.ErrorInDatastore.New(err)
	}

	// グループに所属するユーザー情報一覧取得
	us := map[string]*user.User{}

	for _, userID := range g.UserIDs {
		u, err := ps.apiClient.ShowUser(ctx, userID)
		if err != nil {
			return nil, domain.ErrorInDatastore.New(err)
		}

		us[userID] = u
	}

	// 支払い情報一覧取得
	payments, err := ps.paymentRepository.Index(ctx, groupID)
	if err != nil {
		return nil, domain.ErrorInDatastore.New(err)
	}

	for _, payment := range payments {
		for i, payer := range payment.Payers {
			u := us[payer.ID]
			if u == nil {
				payment.Payers[i].Name = "Unknown User"
			} else {
				payment.Payers[i].Name = us[payer.ID].Name
			}
		}
	}

	return payments, nil
}

func (ps *paymentService) Show(ctx context.Context, groupID string, paymentID string) (*payment.Payment, error) {
	p, err := ps.paymentRepository.Show(ctx, groupID, paymentID)
	if err != nil {
		err = xerrors.Errorf("Failed to Repository: %w", err)
		return nil, domain.NotFound.New(err)
	}

	for i, payer := range p.Payers {
		if u, _ := ps.apiClient.ShowUser(ctx, payer.ID); u == nil {
			p.Payers[i].Name = "Unknown User"
		} else {
			p.Payers[i].Name = u.Name
		}
	}

	return p, nil
}

func (ps *paymentService) Create(ctx context.Context, p *payment.Payment, groupID string) (*payment.Payment, error) {
	if ves := ps.paymentDomainValidation.Payment(ctx, p); len(ves) > 0 {
		err := xerrors.New("Failed to DomainValidation")
		return nil, domain.Unknown.New(err, ves...)
	}

	// 支払い情報登録
	current := time.Now()

	p.ID = uuid.New().String()
	p.IsCompleted = false
	p.CreatedAt = current
	p.UpdatedAt = current

	// フラグの追加
	for i, payer := range p.Payers {
		if payer == nil {
			err := xerrors.New("Failed to Service: Payer is nil")
			return nil, domain.Unknown.New(err)
		}

		p.Payers[i].IsPaid = payer.Amount >= 0
	}

	if err := ps.paymentRepository.Create(ctx, p, groupID); err != nil {
		err = xerrors.Errorf("Failed to Repository: %w", err)
		return nil, domain.ErrorInDatastore.New(err)
	}

	// 支払い情報登録の通知
	deviceTokens := []string{}
	for _, payer := range p.Payers {
		u, _ := ps.apiClient.ShowUser(ctx, payer.ID)
		if u == nil || u.InstanceID == "" {
			continue
		}

		deviceTokens = append(deviceTokens, u.InstanceID)
	}

	message := "新しい支払い情報が追加されました."

	if err := ps.notificationClient.Send(ctx, deviceTokens, p.Name, message); err != nil {
		err = xerrors.Errorf("Failed to Firebase Cloud Messaging: %w", err)
		return nil, domain.Unknown.New(err)
	}

	return p, nil
}

func (ps *paymentService) Update(ctx context.Context, p *payment.Payment, groupID string) (*payment.Payment, error) {
	if ves := ps.paymentDomainValidation.Payment(ctx, p); len(ves) > 0 {
		err := xerrors.New("Failed to DomainValidation")
		return nil, domain.Unknown.New(err, ves...)
	}

	current := time.Now()
	p.UpdatedAt = current

	// 支払い完了フラグの更新
	// - isCompletedがtrue -> isPaidをtrueに
	// - isCompletedがfalse -> isPaidがすべてtrueなら、isCompletedをtrueに
	if p.IsCompleted {
		for i := 0; i < len(p.Payers); i++ {
			p.Payers[i].IsPaid = true
		}
	} else {
		for i, payer := range p.Payers {
			if !payer.IsPaid {
				break
			}

			if i == len(p.Payers)-1 {
				p.IsCompleted = true
			}
		}
	}

	if err := ps.paymentRepository.Update(ctx, p, groupID); err != nil {
		err = xerrors.Errorf("Failed to Repository: %w", err)
		return nil, domain.ErrorInDatastore.New(err)
	}

	return p, nil
}

func (ps *paymentService) UpdatePayer(
	ctx context.Context, groupID string, paymentID string, payer *payment.Payer,
) (*payment.Payment, error) {
	p, err := ps.paymentRepository.Show(ctx, groupID, paymentID)
	if err != nil {
		err = xerrors.Errorf("Failed to Repository: %w", err)
		return nil, domain.NotFound.New(err)
	}

	current := time.Now()
	p.UpdatedAt = current

	// ユーザ毎の支払い情報更新
	for i, v := range p.Payers {
		if v.ID != payer.ID {
			continue
		}

		p.Payers[i].IsPaid = payer.IsPaid
	}

	// 支払い完了フラグの更新
	for i, payer := range p.Payers {
		if !payer.IsPaid {
			p.IsCompleted = false
			break
		}

		if i == len(p.Payers)-1 {
			p.IsCompleted = true
		}
	}

	if err := ps.paymentRepository.Update(ctx, p, groupID); err != nil {
		err = xerrors.Errorf("Failed to Repository: %w", err)
		return nil, domain.ErrorInDatastore.New(err)
	}

	return p, nil
}

func (ps *paymentService) UploadImage(ctx context.Context, data []byte) (string, error) {
	imageURL, err := ps.paymentUploader.UploadImage(ctx, data)
	if err != nil {
		err = xerrors.Errorf("Failed to Uploader: %w", err)
		return "", domain.ErrorInStorage.New(err)
	}

	return imageURL, nil
}
