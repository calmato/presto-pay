package payment

import "context"

// PaymentRepository - PaymentRepositoryインターフェース
type PaymentRepository interface {
	Index(ctx context.Context, groupID string) ([]*Payment, error)
	Create(ctx context.Context, p *Payment, groupID string) error
}
