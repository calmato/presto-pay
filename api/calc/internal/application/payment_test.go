package application

import (
	"context"
	"testing"

	"github.com/calmato/presto-pay/api/calc/internal/application/request"
	"github.com/calmato/presto-pay/api/calc/internal/domain"
	"github.com/calmato/presto-pay/api/calc/internal/domain/payment"
	"github.com/calmato/presto-pay/api/calc/internal/domain/user"
	mock_validation "github.com/calmato/presto-pay/api/calc/mock/application/validation"
	mock_payment "github.com/calmato/presto-pay/api/calc/mock/domain/payment"
	mock_user "github.com/calmato/presto-pay/api/calc/mock/domain/user"
	"github.com/golang/mock/gomock"
)

func TestPaymentApplication_Create(t *testing.T) {
	testCases := map[string]struct {
		Request *request.CreatePayment
		GroupID string
	}{
		"ok": {
			Request: &request.CreatePayment{
				Name:     "テスト支払い",
				Currency: "dollar",
				Total:    12000,
				Tags:     []string{},
				Comment:  "",
				Images:   []string{},
				Payers:   []*request.PayerInCreatePayment{},
			},
			GroupID: "group-id",
		},
	}

	for result, testCase := range testCases {
		ctx, cancel := context.WithCancel(context.Background())
		defer cancel()

		ctrl := gomock.NewController(t)
		defer ctrl.Finish()

		// Defined variables
		ves := make([]*domain.ValidationError, 0)

		u := &user.User{
			ID:       "user-id",
			GroupIDs: []string{"group-id"},
		}

		p := &payment.Payment{
			Name:      testCase.Request.Name,
			Currency:  testCase.Request.Currency,
			Total:     testCase.Request.Total,
			Tags:      testCase.Request.Tags,
			Comment:   testCase.Request.Comment,
			ImageURLs: []string{},
			Payers:    []*payment.Payer{},
		}

		// Defined mocks
		prvm := mock_validation.NewMockPaymentRequestValidation(ctrl)
		prvm.EXPECT().CreatePayment(testCase.Request).Return(ves)

		usm := mock_user.NewMockUserService(ctrl)
		usm.EXPECT().Authentication(ctx).Return(u, nil)
		usm.EXPECT().ContainsGroupID(ctx, u, testCase.GroupID).Return(true, nil)

		psm := mock_payment.NewMockPaymentService(ctrl)
		psm.EXPECT().Create(ctx, p, testCase.GroupID).Return(p, nil)

		// Start test
		t.Run(result, func(t *testing.T) {
			target := NewPaymentApplication(prvm, usm, psm)

			_, err := target.Create(ctx, testCase.Request, testCase.GroupID)
			if err != nil {
				t.Fatalf("error: %v", err)
				return
			}
		})
	}
}
