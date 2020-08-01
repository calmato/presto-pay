package payment

import "context"

// PaymentService - PaymentServiceインターフェース
type PaymentService interface {
	Create(ctx context.Context, p *Payment, groupID string) (*Payment, error)
	UploadImage(ctx context.Context, data []byte) (string, error)
}
