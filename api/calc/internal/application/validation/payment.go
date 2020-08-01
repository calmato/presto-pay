package validation

import (
	"github.com/calmato/presto-pay/api/calc/internal/application/request"
	"github.com/calmato/presto-pay/api/calc/internal/domain"
)

// PaymentRequestValidation - 支払い関連のバリデーション
type PaymentRequestValidation interface {
	CreatePayment(req *request.CreatePayment) []*domain.ValidationError
}

type paymentRequestValidation struct {
	validator RequestValidator
}

// NewPaymentRequestValdation - PaymentRequestValidationの生成
func NewPaymentRequestValidation() PaymentRequestValidation {
	rv := NewRequestValidator()

	return &paymentRequestValidation{
		validator: rv,
	}
}

func (prv *paymentRequestValidation) CreatePayment(req *request.CreatePayment) []*domain.ValidationError {
	return prv.validator.Run(req)
}
