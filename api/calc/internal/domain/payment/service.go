package payment

import "context"

// PaymentService - PaymentServiceインターフェース
type PaymentService interface {
	Index(ctx context.Context, groupID string) ([]*Payment, error)
	Create(ctx context.Context, p *Payment, groupID string) (*Payment, error)
	UploadImage(ctx context.Context, data []byte) (string, error)
}
