package payment

import (
	"context"

	"github.com/calmato/presto-pay/api/calc/internal/domain"
)

// PaymentDomainValidation - PaymentDomainValidationインターフェース
type PaymentDomainValidation interface {
	Payment(ctx context.Context, p *Payment) []*domain.ValidationError
}
