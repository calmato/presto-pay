package service

import (
	"context"
	"reflect"
	"testing"
	"time"

	"github.com/calmato/presto-pay/api/calc/internal/domain/group"
	"github.com/calmato/presto-pay/api/calc/internal/domain/payment"
	"github.com/calmato/presto-pay/api/calc/internal/domain/user"
	mock_group "github.com/calmato/presto-pay/api/calc/mock/domain/group"
	mock_payment "github.com/calmato/presto-pay/api/calc/mock/domain/payment"
	mock_api "github.com/calmato/presto-pay/api/calc/mock/infrastructure/api"
	mock_notification "github.com/calmato/presto-pay/api/calc/mock/infrastructure/notification"
	"github.com/golang/mock/gomock"
)

// TODO: create test for index

func TestPaymentService_Create(t *testing.T) {
	current := time.Now()

	testCases := map[string]struct {
		Payment *payment.Payment
		GroupID string
	}{
		"ok": {
			Payment: &payment.Payment{
				Name:      "支払いテスト",
				Currency:  "dollar",
				Total:     15000,
				Payers:    []*payment.Payer{},
				Tags:      []string{},
				Comment:   "",
				ImageURLs: []string{},
				PaidAt:    current,
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
		g := &group.Group{
			ID: testCase.GroupID,
		}

		// Defined mocks
		pdvm := mock_payment.NewMockPaymentDomainValidation(ctrl)
		pdvm.EXPECT().Payment(ctx, testCase.Payment).Return(nil)

		prm := mock_payment.NewMockPaymentRepository(ctrl)
		prm.EXPECT().Create(ctx, testCase.Payment, testCase.GroupID).Return(nil)

		pum := mock_payment.NewMockPaymentUploader(ctrl)

		grm := mock_group.NewMockGroupRepository(ctrl)
		grm.EXPECT().Show(ctx, testCase.GroupID).Return(g, nil)
		grm.EXPECT().Update(ctx, g).Return(nil)

		acm := mock_api.NewMockAPIClient(ctrl)
		for _, payer := range testCase.Payment.Payers {
			u := &user.User{
				ID: payer.ID,
			}

			acm.EXPECT().ShowUser(ctrl, payer.ID).Return(u, nil)
		}

		ncm := mock_notification.NewMockNotificationClient(ctrl)
		ncm.
			EXPECT().
			Send(ctx, []string{}, testCase.Payment.Name, "新しい支払い情報が追加されました.").
			Return(nil)

		// Start test
		t.Run(result, func(t *testing.T) {
			target := NewPaymentService(pdvm, prm, pum, grm, acm, ncm)

			_, err := target.Create(ctx, testCase.Payment, testCase.GroupID)
			if err != nil {
				t.Fatalf("error: %v", err)
				return
			}
		})
	}
}

func TestPaymentService_UploadImage(t *testing.T) {
	testCases := map[string]struct {
		Data     []byte
		Expected string
	}{
		"ok": {
			Data:     []byte{0xDe, 0xaD, 0xBe, 0xeF},
			Expected: "http://localhost:8080",
		},
	}

	for result, testCase := range testCases {
		ctx, cancel := context.WithCancel(context.Background())
		defer cancel()

		ctrl := gomock.NewController(t)
		defer ctrl.Finish()

		// Defined mocks
		pdvm := mock_payment.NewMockPaymentDomainValidation(ctrl)

		prm := mock_payment.NewMockPaymentRepository(ctrl)

		pum := mock_payment.NewMockPaymentUploader(ctrl)
		pum.EXPECT().UploadImage(ctx, testCase.Data).Return(testCase.Expected, nil)

		acm := mock_api.NewMockAPIClient(ctrl)

		grm := mock_group.NewMockGroupRepository(ctrl)

		ncm := mock_notification.NewMockNotificationClient(ctrl)

		// Start test
		t.Run(result, func(t *testing.T) {
			target := NewPaymentService(pdvm, prm, pum, grm, acm, ncm)

			got, err := target.UploadImage(ctx, testCase.Data)
			if err != nil {
				t.Fatalf("error: %v", err)
				return
			}

			if !reflect.DeepEqual(got, testCase.Expected) {
				t.Fatalf("want %#v, but %#v", testCase.Expected, got)
				return
			}
		})
	}
}
