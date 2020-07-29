package validation

import (
	"context"
	"reflect"
	"testing"
	"time"

	"github.com/calmato/presto-pay/api/calc/internal/domain"
	"github.com/calmato/presto-pay/api/calc/internal/domain/payment"
	"github.com/calmato/presto-pay/api/calc/internal/domain/user"
	mock_api "github.com/calmato/presto-pay/api/calc/mock/infrastructure/api"
	"github.com/golang/mock/gomock"
)

func TestPaymentValidation_Payment(t *testing.T) {
	current := time.Now()

	testCases := map[string]struct {
		Payment  *payment.Payment
		Expected []*domain.ValidationError
	}{
		"ok": {
			Payment: &payment.Payment{
				ID:       "payment-id",
				Name:     "支払いテスト",
				Currency: "dollar",
				Total:    15000,
				Payers: []*payment.Payer{
					{
						ID:     "user-id",
						Amount: 0,
					},
				},
				Tags:      []string{},
				Comment:   "",
				ImageURLs: []string{},
				PaidAt:    current,
				CreatedAt: current,
				UpdatedAt: current,
			},
			Expected: make([]*domain.ValidationError, 0),
		},
	}

	for result, testCase := range testCases {
		ctx, cancel := context.WithCancel(context.Background())
		defer cancel()

		ctrl := gomock.NewController(t)
		defer ctrl.Finish()

		// Defined variables
		u := &user.User{
			ID: "user-id",
		}

		// Defined mocks
		acm := mock_api.NewMockAPIClient(ctrl)
		acm.EXPECT().UserExists(ctx, u.ID).Return(true, nil)

		t.Run(result, func(t *testing.T) {
			target := NewPaymentDomainValidation(acm)

			got := target.Payment(ctx, testCase.Payment)
			if !reflect.DeepEqual(got, testCase.Expected) {
				t.Fatalf("want %#v, but %#v", testCase.Expected, got)
				return
			}
		})
	}
}
