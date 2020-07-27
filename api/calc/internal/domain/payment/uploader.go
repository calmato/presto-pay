package payment

import "context"

// PaymentUploader - PaymentUploaderインターフェース
type PaymentUploader interface {
	UploadImage(ctx context.Context, data []byte) (string, error)
}
