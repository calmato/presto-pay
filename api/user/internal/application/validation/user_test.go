package validation

import (
	"reflect"
	"testing"

	"github.com/calmato/presto-pay/api/user/internal/application/request"
	"github.com/calmato/presto-pay/api/user/internal/domain"
)

func TestUserRequestValidation_CreateUser(t *testing.T) {
	testCases := map[string]struct {
		Request  *request.CreateUser
		Expected []*domain.ValidationError
	}{
		"ok": {
			Request: &request.CreateUser{
				Name:                 "test-user",
				DisplayName:          "testUser",
				Email:                "test@calmato.com",
				Thumbnail:            "",
				Password:             "12345678",
				PasswordConfirmation: "12345678",
			},
			Expected: make([]*domain.ValidationError, 0),
		},
	}

	for result, testCase := range testCases {
		// Start test
		t.Run(result, func(t *testing.T) {
			target := NewUserRequestValidation()

			got := target.CreateUser(testCase.Request)
			if !reflect.DeepEqual(got, testCase.Expected) {
				t.Fatalf("want %#v, but %#v", testCase.Expected, got)
				return
			}
		})
	}
}
