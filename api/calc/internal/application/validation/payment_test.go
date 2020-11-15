package validation

import (
	"reflect"
	"testing"
	"time"

	"github.com/calmato/presto-pay/api/calc/internal/application/request"
	"github.com/calmato/presto-pay/api/calc/internal/domain"
)

func TestPaymentRequestValidation_CreatePayment(t *testing.T) {
	current := time.Now()

	testCases := map[string]struct {
		Request  *request.CreatePayment
		Expected []*domain.ValidationError
	}{
		"ok": {
			Request: &request.CreatePayment{
				Name:     "payment-test",
				Currency: "dollar",
				PositivePayers: []*request.PayerInCreatePayment{
					{
						ID:     "test-user01",
						Amount: 100,
					},
					{
						ID:     "test-user02",
						Amount: -100,
					},
				},
				NegativePayers: []*request.PayerInCreatePayment{},
				Tags:           []string{},
				Comment:        "",
				Images:         []string{},
				PaidAt:         current,
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
