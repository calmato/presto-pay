package payment

import "context"

// PaymentService - PaymentServiceインターフェース
type PaymentService interface {
	Index(ctx context.Context, groupID string, startAt string) ([]*Payment, error)
	Create(ctx context.Context, p *Payment, groupID string) (*Payment, error)
	Update(ctx context.Context, p *Payment, groupID string) (*Payment, error)
	UpdatePayers(ctx context.Context, groupID string, paymentID string, payer *Payer) (*Payment, error)
	UploadImage(ctx context.Context, data []byte) (string, error)
}
