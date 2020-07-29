package validation

import (
	"context"

	"github.com/calmato/presto-pay/api/calc/internal/domain"
	"github.com/calmato/presto-pay/api/calc/internal/domain/payment"
	"github.com/calmato/presto-pay/api/calc/internal/infrastructure/api"
	"golang.org/x/xerrors"
)

type paymentDomainValidation struct {
	apiClient api.APIClient
}

// NewPaymentDomainValidation - PaymentDomainValidationの生成
func NewPaymentDomainValidation(ac api.APIClient) payment.PaymentDomainValidation {
	return &paymentDomainValidation{
		apiClient: ac,
	}
}

func (pdv *paymentDomainValidation) Payment(ctx context.Context, p *payment.Payment) []*domain.ValidationError {
	validationErrors := make([]*domain.ValidationError, 0)

	if err := uniqueCheckTags(p.Tags); err != nil {
		validationError := &domain.ValidationError{
			Field:   "tags",
			Message: domain.CustomUniqueMessage,
		}

		validationErrors = append(validationErrors, validationError)
	}

	if err := uniqueCheckPayers(p.Payers); err != nil {
		validationError := &domain.ValidationError{
			Field:   "payers",
			Message: domain.CustomUniqueMessage,
		}

		validationErrors = append(validationErrors, validationError)
	}

	for _, payer := range p.Payers {
		if !userIDExists(ctx, pdv.apiClient, payer.ID) {
			validationError := &domain.ValidationError{
				Field:   "payers",
				Message: domain.CustomNotExistsMessage,
			}

			validationErrors = append(validationErrors, validationError)
			break
		}
	}

	return validationErrors
}

func uniqueCheckPayers(payers []*payment.Payer) error {
	m := make(map[string]struct{})

	for _, v := range payers {
		if v == nil {
			return xerrors.New("There are empty values.")
		}

		if _, ok := m[v.ID]; ok {
			return xerrors.New("There are duplicate values.")
		}

		m[v.ID] = struct{}{}
	}

	return nil
}
