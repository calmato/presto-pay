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

var (
	unknownUsername = "Unknown User"
)

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
			return nil, err
		}

		us[userID] = u
	}

	// 支払い情報一覧取得
	payments, err := ps.paymentRepository.Index(ctx, groupID)
	if err != nil {
		return nil, domain.ErrorInDatastore.New(err)
	}

	for i, payment := range payments {
		payments[i].GroupID = groupID

		for j, payer := range payment.Payers {
			u := us[payer.ID]
			if u == nil {
				payment.Payers[j].Name = unknownUsername
			} else {
				payment.Payers[j].Name = us[payer.ID].Name
			}
		}
	}

	return payments, nil
}

func (ps *paymentService) IndexByIsCompleted(
	ctx context.Context, groupID string, isCompleted bool,
) ([]*payment.Payment, error) {
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
			return nil, err
		}

		us[userID] = u
	}

	// 支払い情報一覧取得
	payments, err := ps.paymentRepository.IndexByIsCompleted(ctx, groupID, isCompleted)
	if err != nil {
		return nil, domain.ErrorInDatastore.New(err)
	}

	for i, payment := range payments {
		payments[i].GroupID = groupID

		for j, payer := range payment.Payers {
			u := us[payer.ID]
			if u == nil {
				payment.Payers[j].Name = unknownUsername
			} else {
				payment.Payers[j].Name = us[payer.ID].Name
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

	p.GroupID = groupID

	for i, payer := range p.Payers {
		if u, _ := ps.apiClient.ShowUser(ctx, payer.ID); u == nil {
			p.Payers[i].Name = unknownUsername
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

	// グループ情報取得 -> 存在性の検証
	g, err := ps.groupRepository.Show(ctx, groupID)
	if err != nil {
		return nil, domain.ErrorInDatastore.New(err)
	}

	// 支払い情報登録
	current := time.Now()

	p.ID = uuid.New().String()
	p.GroupID = g.ID
	p.IsCompleted = false
	p.CreatedAt = current
	p.UpdatedAt = current

	checkPaid(p, current)

	if err := ps.paymentRepository.Create(ctx, p, groupID); err != nil {
		err = xerrors.Errorf("Failed to Repository: %w", err)
		return nil, domain.ErrorInDatastore.New(err)
	}

	// 支払い情報登録の通知
	deviceTokens := []string{}
	for _, py := range p.Payers {
		u, _ := ps.apiClient.ShowUser(ctx, py.ID)
		if u == nil || u.InstanceID == "" {
			continue
		}

		deviceTokens = append(deviceTokens, u.InstanceID)
	}

	// 支払い情報登録時を、グループの最終更新日時に変更
	g.UpdatedAt = current

	if err := ps.groupRepository.Update(ctx, g); err != nil {
		err = xerrors.Errorf("Failed to Repository: %w", err)
		return nil, domain.ErrorInDatastore.New(err)
	}

	if err := ps.notificationClient.Send(ctx, deviceTokens, p.Name, domain.CreatePaymentNotification); err != nil {
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

	// グループ情報取得 -> 存在性の検証
	g, err := ps.groupRepository.Show(ctx, groupID)
	if err != nil {
		return nil, domain.ErrorInDatastore.New(err)
	}

	// 支払い情報の更新
	current := time.Now()

	p.GroupID = g.ID
	p.UpdatedAt = current

	checkPaid(p, current)

	if err := ps.paymentRepository.Update(ctx, p, groupID); err != nil {
		err = xerrors.Errorf("Failed to Repository: %w", err)
		return nil, domain.ErrorInDatastore.New(err)
	}

	// 支払い情報更新時を、グループの最終更新日時に変更
	g.UpdatedAt = current

	if err := ps.groupRepository.Update(ctx, g); err != nil {
		err = xerrors.Errorf("Failed to Repository: %w", err)
		return nil, domain.ErrorInDatastore.New(err)
	}

	// 支払い情報更新の通知
	deviceTokens := []string{}
	for _, py := range p.Payers {
		u, _ := ps.apiClient.ShowUser(ctx, py.ID)
		if u == nil || u.InstanceID == "" {
			continue
		}

		deviceTokens = append(deviceTokens, u.InstanceID)
	}

	if err := ps.notificationClient.Send(ctx, deviceTokens, p.Name, domain.UpdatePaymentNotification); err != nil {
		err = xerrors.Errorf("Failed to Firebase Cloud Messaging: %w", err)
		return nil, domain.Unknown.New(err)
	}

	return p, nil
}

func (ps *paymentService) UpdatePayer(
	ctx context.Context, p *payment.Payment, groupID string, payerID string, isPaid bool,
) (*payment.Payment, error) {
	// グループ情報取得 -> 存在性の検証
	g, err := ps.groupRepository.Show(ctx, groupID)
	if err != nil {
		return nil, domain.ErrorInDatastore.New(err)
	}

	// 支払い情報の更新
	current := time.Now()

	p.GroupID = g.ID
	p.UpdatedAt = current

	// 対象ユーザーの支払い情報更新
	for _, py := range p.Payers {
		if payerID != py.ID {
			continue
		}

		if isPaid {
			py.PaidAt = current
		} else {
			py.PaidAt = time.Time{}
		}

		break
	}

	// 支払い完了フラグの更新
	for i, py := range p.Payers {
		if py.PaidAt.IsZero() {
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

	// 支払い情報更新時を、グループの最終更新日時に変更
	g.UpdatedAt = current

	if err := ps.groupRepository.Update(ctx, g); err != nil {
		err = xerrors.Errorf("Failed to Repository: %w", err)
		return nil, domain.ErrorInDatastore.New(err)
	}

	// 支払い情報更新の通知
	deviceTokens := []string{}
	for _, payer := range p.Payers {
		u, _ := ps.apiClient.ShowUser(ctx, payer.ID)
		if u == nil || u.InstanceID == "" {
			continue
		}

		deviceTokens = append(deviceTokens, u.InstanceID)
	}

	if err := ps.notificationClient.Send(ctx, deviceTokens, p.Name, domain.UpdatePaymentNotification); err != nil {
		err = xerrors.Errorf("Failed to Firebase Cloud Messaging: %w", err)
		return nil, domain.Unknown.New(err)
	}

	return p, nil
}

func (ps *paymentService) Destroy(ctx context.Context, groupID string, paymentID string) error {
	// グループ情報取得 -> 存在性の検証
	g, err := ps.groupRepository.Show(ctx, groupID)
	if err != nil {
		return domain.ErrorInDatastore.New(err)
	}

	if err := ps.paymentRepository.Destroy(ctx, g.ID, paymentID); err != nil {
		err = xerrors.Errorf("Failed to Repository: %w", err)
		return domain.ErrorInDatastore.New(err)
	}

	// 支払い情報更新時を、グループの最終更新日時に変更
	current := time.Now()

	g.UpdatedAt = current

	if err := ps.groupRepository.Update(ctx, g); err != nil {
		err = xerrors.Errorf("Failed to Repository: %w", err)
		return domain.ErrorInDatastore.New(err)
	}

	return nil
}

func (ps *paymentService) UploadImage(ctx context.Context, data []byte) (string, error) {
	imageURL, err := ps.paymentUploader.UploadImage(ctx, data)
	if err != nil {
		err = xerrors.Errorf("Failed to Uploader: %w", err)
		return "", domain.ErrorInStorage.New(err)
	}

	return imageURL, nil
}

// checkPaid - 支払い完了フラグの更新
func checkPaid(p *payment.Payment, current time.Time) {
	// 支払い者全員を支払い完了に
	if p.IsCompleted {
		for _, py := range p.Payers {
			if py.PaidAt.IsZero() {
				py.PaidAt = current
			}
		}

		return
	}

	// 各支払い者の支払い日時を更新
	// -> 受け取る側: PaitAtがゼロ値の場合、支払い完了日時を追加
	// -> 支払う側: PaidAtを一律ゼロ値に
	for _, py := range p.Payers {
		if py.IsPaid {
			if py.PaidAt.IsZero() {
				py.PaidAt = current
			}
		} else {
			py.PaidAt = time.Time{}
		}
	}
}
