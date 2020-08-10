package validation

import (
	"reflect"
	"testing"

	"github.com/calmato/presto-pay/api/user/internal/application/request"
	"github.com/calmato/presto-pay/api/user/internal/domain"
)

func TestUserRequestValidation_IndexByUsername(t *testing.T) {
	testCases := map[string]struct {
		Request  *request.IndexByUsername
		Expected []*domain.ValidationError
	}{
		"ok": {
			Request: &request.IndexByUsername{
				Username: "test-user",
				StartAt:  "",
			},
			Expected: make([]*domain.ValidationError, 0),
		},
	}

	for result, testCase := range testCases {
		// Start test
		t.Run(result, func(t *testing.T) {
			target := NewUserRequestValidation()

			got := target.IndexByUsername(testCase.Request)
			if !reflect.DeepEqual(got, testCase.Expected) {
				t.Fatalf("want %#v, but %#v", testCase.Expected, got)
				return
			}
		})
	}
}

func TestUserRequestValidation_CreateUser(t *testing.T) {
	testCases := map[string]struct {
		Request  *request.CreateUser
		Expected []*domain.ValidationError
	}{
		"ok": {
			Request: &request.CreateUser{
				Name:                 "テストユーザー",
				Username:             "test-user",
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

func TestUserRequestValidation_RegisterInstanceID(t *testing.T) {
	testCases := map[string]struct {
		Request  *request.RegisterInstanceID
		Expected []*domain.ValidationError
	}{
		"ok": {
			Request: &request.RegisterInstanceID{
				InstanceID: "instance-id",
			},
			Expected: make([]*domain.ValidationError, 0),
		},
	}

	for result, testCase := range testCases {
		// Start test
		t.Run(result, func(t *testing.T) {
			target := NewUserRequestValidation()

			got := target.RegisterInstanceID(testCase.Request)
			if !reflect.DeepEqual(got, testCase.Expected) {
				t.Fatalf("want %#v, but %#v", testCase.Expected, got)
				return
			}
		})
	}
}

func TestUserRequestValidation_UpdateProfile(t *testing.T) {
	testCases := map[string]struct {
		Request  *request.UpdateProfile
		Expected []*domain.ValidationError
	}{
		"ok": {
			Request: &request.UpdateProfile{
				Name:      "テストユーザー",
				Username:  "test-user",
				Email:     "test@calmato.com",
				Thumbnail: "",
			},
			Expected: make([]*domain.ValidationError, 0),
		},
	}

	for result, testCase := range testCases {
		// Start test
		t.Run(result, func(t *testing.T) {
			target := NewUserRequestValidation()

			got := target.UpdateProfile(testCase.Request)
			if !reflect.DeepEqual(got, testCase.Expected) {
				t.Fatalf("want %#v, but %#v", testCase.Expected, got)
				return
			}
		})
	}
}

func TestUserRequestValidation_UpdatePassword(t *testing.T) {
	testCases := map[string]struct {
		Request  *request.UpdateUserPassword
		Expected []*domain.ValidationError
	}{
		"ok": {
			Request: &request.UpdateUserPassword{
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

			got := target.UpdatePassword(testCase.Request)
			if !reflect.DeepEqual(got, testCase.Expected) {
				t.Fatalf("want %#v, but %#v", testCase.Expected, got)
				return
			}
		})
	}
}

func TestUserRequestValidation_AddFriend(t *testing.T) {
	testCases := map[string]struct {
		Request  *request.AddFriend
		Expected []*domain.ValidationError
	}{
		"ok": {
			Request: &request.AddFriend{
				UserID: "user-id",
			},
			Expected: make([]*domain.ValidationError, 0),
		},
	}

	for result, testCase := range testCases {
		// Start test
		t.Run(result, func(t *testing.T) {
			target := NewUserRequestValidation()

			got := target.AddFriend(testCase.Request)
			if !reflect.DeepEqual(got, testCase.Expected) {
				t.Fatalf("want %#v, but %#v", testCase.Expected, got)
				return
			}
		})
	}
}

func TestUserRequestValidation_UniqueCheckEmail(t *testing.T) {
	testCases := map[string]struct {
		Request  *request.UniqueCheckUserEmail
		Expected []*domain.ValidationError
	}{
		"ok": {
			Request: &request.UniqueCheckUserEmail{
				Email: "test@calmato.work",
			},
			Expected: make([]*domain.ValidationError, 0),
		},
	}

	for result, testCase := range testCases {
		// Start test
		t.Run(result, func(t *testing.T) {
			target := NewUserRequestValidation()

			got := target.UniqueCheckEmail(testCase.Request)
			if !reflect.DeepEqual(got, testCase.Expected) {
				t.Fatalf("want %#v, but %#v", testCase.Expected, got)
				return
			}
		})
	}
}

func TestUserRequestValidation_UniqueCheckUsername(t *testing.T) {
	testCases := map[string]struct {
		Request  *request.UniqueCheckUserUsername
		Expected []*domain.ValidationError
	}{
		"ok": {
			Request: &request.UniqueCheckUserUsername{
				Username: "test-user",
			},
			Expected: make([]*domain.ValidationError, 0),
		},
	}

	for result, testCase := range testCases {
		// Start test
		t.Run(result, func(t *testing.T) {
			target := NewUserRequestValidation()

			got := target.UniqueCheckUsername(testCase.Request)
			if !reflect.DeepEqual(got, testCase.Expected) {
				t.Fatalf("want %#v, but %#v", testCase.Expected, got)
				return
			}
		})
	}
}
