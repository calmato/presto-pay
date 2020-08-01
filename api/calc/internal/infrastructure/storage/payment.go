package storage

import (
	"context"

	"github.com/calmato/presto-pay/api/calc/internal/domain/payment"
	gcs "github.com/calmato/presto-pay/api/calc/lib/firebase/storage"
)

type paymentUploader struct {
	storage *gcs.Storage
}

// NewPaymentUploader - PaymentUploaderの生成
func NewPaymentUploader(cs *gcs.Storage) payment.PaymentUploader {
	return &paymentUploader{
		storage: cs,
	}
}

func (pu *paymentUploader) UploadImage(ctx context.Context, data []byte) (string, error) {
	thumbnailURL, err := pu.storage.Write(ctx, PaymentImagePath, data)
	if err != nil {
		return "", err
	}

	return thumbnailURL, nil
}
