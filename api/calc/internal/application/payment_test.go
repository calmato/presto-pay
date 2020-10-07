package application

import (
	"fmt"
	"testing"
)

func Test_Temp(t *testing.T) {
	fmt.Println("Hello, World")
}

// FIX: 後でテスト修正
// -> 修正内容: RedisClientのMock追加
// func TestPaymentApplication_Create(t *testing.T) {
// 	testCases := map[string]struct {
// 		Request *request.CreatePayment
// 		GroupID string
// 	}{
// 		"ok": {
// 			Request: &request.CreatePayment{
// 				Name:     "テスト支払い",
// 				Currency: "dollar",
// 				Total:    12000,
// 				Tags:     []string{},
// 				Comment:  "",
// 				Images:   []string{},
// 				Payers:   []*request.PayerInCreatePayment{},
// 			},
// 			GroupID: "group-id",
// 		},
// 	}

// 	for result, testCase := range testCases {
// 		ctx, cancel := context.WithCancel(context.Background())
// 		defer cancel()

// 		ctrl := gomock.NewController(t)
// 		defer ctrl.Finish()

// 		// Defined variables
// 		ves := make([]*domain.ValidationError, 0)

// 		u := &user.User{
// 			ID:       "user-id",
// 			GroupIDs: []string{"group-id"},
// 		}

// 		p := &payment.Payment{
// 			Name:      testCase.Request.Name,
// 			Currency:  testCase.Request.Currency,
// 			Total:     testCase.Request.Total,
// 			Tags:      testCase.Request.Tags,
// 			Comment:   testCase.Request.Comment,
// 			ImageURLs: []string{},
// 			Payers:    []*payment.Payer{},
// 		}

// 		// Defined mocks
// 		prvm := mock_validation.NewMockPaymentRequestValidation(ctrl)
// 		prvm.EXPECT().CreatePayment(testCase.Request).Return(ves)

// 		usm := mock_user.NewMockUserService(ctrl)
// 		usm.EXPECT().Authentication(ctx).Return(u, nil)
// 		usm.EXPECT().ContainsGroupID(ctx, u, testCase.GroupID).Return(true, nil)

// 		psm := mock_payment.NewMockPaymentService(ctrl)
// 		psm.EXPECT().Create(ctx, p, testCase.GroupID).Return(p, nil)

// 		// Start test
// 		t.Run(result, func(t *testing.T) {
// 			target := NewPaymentApplication(prvm, usm, psm)

// 			_, err := target.Create(ctx, testCase.Request, testCase.GroupID)
// 			if err != nil {
// 				t.Fatalf("error: %v", err)
// 				return
// 			}
// 		})
// 	}
// }
