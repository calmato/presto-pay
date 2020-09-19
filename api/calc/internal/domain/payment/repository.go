package payment

import "context"

// PaymentRepository - PaymentRepositoryインターフェース
type PaymentRepository interface {
	Index(ctx context.Context, groupID string) ([]*Payment, error)
	IndexFromStartAt(ctx context.Context, groupID string, startAt string) ([]*Payment, error)
	Show(ctx context.Context, groupID string, paymentID string) (*Payment, error)
	Create(ctx context.Context, p *Payment, groupID string) error
	Update(ctx context.Context, p *Payment, groupID string) error
}
