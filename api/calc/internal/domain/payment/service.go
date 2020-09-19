package payment

import "context"

// PaymentService - PaymentServiceインターフェース
type PaymentService interface {
	Index(ctx context.Context, groupID string, startAt string) ([]*Payment, error)
	Show(ctx context.Context, groupID string, paymentID string) (*Payment, error)
	Create(ctx context.Context, p *Payment, groupID string) (*Payment, error)
	Update(ctx context.Context, p *Payment, groupID string) (*Payment, error)
	UpdatePayer(ctx context.Context, groupID string, paymentID string, payer *Payer) (*Payment, error)
	UploadImage(ctx context.Context, data []byte) (string, error)
}
