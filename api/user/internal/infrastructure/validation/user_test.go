package validation

import (
	"context"
	"reflect"
	"testing"
	"time"

	"github.com/calmato/presto-pay/api/user/internal/domain"
	"github.com/calmato/presto-pay/api/user/internal/domain/user"
	mock_user "github.com/calmato/presto-pay/api/user/mock/domain/user"
	"github.com/golang/mock/gomock"
)

func TestUserValidation_User(t *testing.T) {
	current := time.Now()

	testCases := map[string]struct {
		User     *user.User
		Expected []*domain.ValidationError
	}{
		"ok": {
			User: &user.User{
				ID:           "user-id",
				Name:         "テストユーザー",
				Username:     "test-user",
				Email:        "test@calmato.com",
				ThumbnailURL: "",
				Password:     "!Qaz2wsx",
				CreatedAt:    current,
				UpdatedAt:    current,
			},
			Expected: make([]*domain.ValidationError, 0),
		},
	}

	for result, testCase := range testCases {
		ctx, cancel := context.WithCancel(context.Background())
		defer cancel()

		ctrl := gomock.NewController(t)
		defer ctrl.Finish()

		// Defined mocks
		urm := mock_user.NewMockUserRepository(ctrl)
		urm.EXPECT().GetUIDByEmail(ctx, testCase.User.Email).Return(testCase.User.ID, nil)

		t.Run(result, func(t *testing.T) {
			target := NewUserDomainValidation(urm)

			got := target.User(ctx, testCase.User)
			if !reflect.DeepEqual(got, testCase.Expected) {
				t.Fatalf("want %#v, but %#v", testCase.Expected, got)
				return
			}
		})
	}
}
