package validation

import (
	"reflect"
	"testing"
	"time"

	"github.com/calmato/presto-pay/api/calc/internal/application/request"
	"github.com/calmato/presto-pay/api/calc/internal/domain"
)

func TestPaymentRequestValidation_CreatePayment(t *testing.T) {
	current := time.Now().Format("2006-01-02 15:04:05")

	testCases := map[string]struct {
		Request  *request.CreatePayment
		Expected []*domain.ValidationError
	}{
		"ok": {
			Request: &request.CreatePayment{
				Name:     "payment-test",
				Currency: "jpy",
				PositivePayers: []*request.PayerInPayment{
					{
						ID:     "test-user01",
						Amount: 100,
					},
					{
						ID:     "test-user02",
						Amount: 100,
					},
				},
				NegativePayers: []*request.PayerInPayment{
					{
						ID:     "test-user03",
						Amount: 200,
					},
				},
				Tags:    []string{"test"},
				Comment: "test comment",
				Images:  []string{},
				PaidAt:  current,
			},
			Expected: make([]*domain.ValidationError, 0),
		},
	}

	for result, testCase := range testCases {
		// Start test
		t.Run(result, func(t *testing.T) {
			target := NewPaymentRequestValidation()

			got := target.CreatePayment(testCase.Request)
			if !reflect.DeepEqual(got, testCase.Expected) {
				t.Fatalf("want %#v, but %#v", testCase.Expected, got)
				return
			}
		})
	}
}
