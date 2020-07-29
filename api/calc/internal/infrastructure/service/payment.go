package service

import (
	"context"
	"time"

	"github.com/calmato/presto-pay/api/calc/internal/domain"
	"github.com/calmato/presto-pay/api/calc/internal/domain/payment"
	"github.com/google/uuid"
	"golang.org/x/xerrors"
)

type paymentService struct {
	paymentDomainValidation payment.PaymentDomainValidation
	paymentRepository       payment.PaymentRepository
	paymentUploader         payment.PaymentUploader
}

// NewPaymentService - PaymentServiceの生成
func NewPaymentService(
	pdv payment.PaymentDomainValidation, pr payment.PaymentRepository, pu payment.PaymentUploader,
) payment.PaymentService {
	return &paymentService{
		paymentDomainValidation: pdv,
		paymentRepository:       pr,
		paymentUploader:         pu,
	}
}

func (ps *paymentService) Create(ctx context.Context, p *payment.Payment, groupID string) (*payment.Payment, error) {
	if ves := ps.paymentDomainValidation.Payment(ctx, p); len(ves) > 0 {
		err := xerrors.New("Failed to DomainValidation")
		return nil, domain.Unknown.New(err, ves...)
	}

	current := time.Now()

	p.ID = uuid.New().String()
	p.CreatedAt = current
	p.UpdatedAt = current

	if err := ps.paymentRepository.Create(ctx, p, groupID); err != nil {
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
