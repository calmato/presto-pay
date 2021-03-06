package validation

import (
	"github.com/calmato/presto-pay/api/calc/internal/application/request"
	"github.com/calmato/presto-pay/api/calc/internal/domain"
)

// PaymentRequestValidation - 支払い関連のバリデーション
type PaymentRequestValidation interface {
	CreatePayment(req *request.CreatePayment) []*domain.ValidationError
	UpdatePayment(req *request.UpdatePayment) []*domain.ValidationError
	UpdatePayerInPayment(req *request.UpdatePayerInPayment) []*domain.ValidationError
}

type paymentRequestValidation struct {
	validator RequestValidator
}

// NewPaymentRequestValidation - PaymentRequestValidationの生成
func NewPaymentRequestValidation() PaymentRequestValidation {
	rv := NewRequestValidator()

	return &paymentRequestValidation{
		validator: rv,
	}
}

func (prv *paymentRequestValidation) CreatePayment(req *request.CreatePayment) []*domain.ValidationError {
	return prv.validator.Run(req)
}

func (prv *paymentRequestValidation) UpdatePayment(req *request.UpdatePayment) []*domain.ValidationError {
	return prv.validator.Run(req)
}

func (prv *paymentRequestValidation) UpdatePayerInPayment(
	req *request.UpdatePayerInPayment,
) []*domain.ValidationError {
	return prv.validator.Run(req)
}
