package application

import (
	"context"
	"testing"

	"github.com/calmato/presto-pay/api/user/internal/application/request"
	"github.com/calmato/presto-pay/api/user/internal/domain"
	"github.com/calmato/presto-pay/api/user/internal/domain/user"
	mock_validation "github.com/calmato/presto-pay/api/user/mock/application/validation"
	mock_user "github.com/calmato/presto-pay/api/user/mock/domain/user"
	"github.com/golang/mock/gomock"
)

func TestUserApplication_Create(t *testing.T) {
	testCases := map[string]struct {
		Request *request.CreateUser
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
			Name:         testCase.Request.Name,
			DisplayName:  testCase.Request.DisplayName,
			Email:        testCase.Request.Email,
			ThumbnailURL: "",
			Password:     testCase.Request.Password,
		}

		// Defined mocks
		urvm := mock_validation.NewMockUserRequestValidation(ctrl)
		urvm.EXPECT().CreateUser(testCase.Request).Return(ves)

		usm := mock_user.NewMockUserService(ctrl)
		usm.EXPECT().Create(ctx, u).Return(nil)

		// Start test
		t.Run(result, func(t *testing.T) {
			target := NewUserApplication(urvm, usm)

			err := target.Create(ctx, testCase.Request)
			if err != nil {
				t.Fatalf("error: %v", err)
				return
			}
		})
	}
}
