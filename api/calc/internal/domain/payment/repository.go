package payment

import "context"

// PaymentRepository - PaymentRepositoryインターフェース
type PaymentRepository interface {
	Create(ctx context.Context, p *Payment, groupID string) error
}
