package application

import (
	"context"
	"reflect"
	"testing"
	"time"

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
				Name:                 "テストユーザー",
				Username:             "test-user",
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
			Username:     testCase.Request.Username,
			Email:        testCase.Request.Email,
			ThumbnailURL: "",
			Password:     testCase.Request.Password,
		}

		// Defined mocks
		urvm := mock_validation.NewMockUserRequestValidation(ctrl)
		urvm.EXPECT().CreateUser(testCase.Request).Return(ves)

		usm := mock_user.NewMockUserService(ctrl)
		usm.EXPECT().Create(ctx, u).Return(u, nil)

		// Start test
		t.Run(result, func(t *testing.T) {
			target := NewUserApplication(urvm, usm)

			_, err := target.Create(ctx, testCase.Request)
			if err != nil {
				t.Fatalf("error: %v", err)
				return
			}
		})
	}
}

func TestUserApplication_Update(t *testing.T) {
	testCases := map[string]struct {
		Request *request.UpdateUser
	}{
		"ok": {
			Request: &request.UpdateUser{
				Name:      "テストユーザー",
				Username:  "test-user",
				Email:     "test@calmato.com",
				Thumbnail: "",
				Language:  "English",
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
			Username:     testCase.Request.Username,
			Email:        testCase.Request.Email,
			ThumbnailURL: "",
			Language:     testCase.Request.Language,
		}

		// Defined mocks
		urvm := mock_validation.NewMockUserRequestValidation(ctrl)
		urvm.EXPECT().UpdateUser(testCase.Request).Return(ves)

		usm := mock_user.NewMockUserService(ctrl)
		usm.EXPECT().Authentication(ctx).Return(u, nil)
		usm.EXPECT().Update(ctx, u).Return(u, nil)

		// Start test
		t.Run(result, func(t *testing.T) {
			target := NewUserApplication(urvm, usm)

			_, err := target.Update(ctx, testCase.Request)
			if err != nil {
				t.Fatalf("error: %v", err)
				return
			}
		})
	}
}

func TestUserApplication_UpdatePassword(t *testing.T) {
	current := time.Now()

	testCases := map[string]struct {
		Request  *request.UpdateUserPassword
		Auth     *user.User
		Expected *user.User
	}{
		"ok": {
			Request: &request.UpdateUserPassword{
				Password:             "12345678",
				PasswordConfirmation: "12345678",
			},
			Auth: &user.User{
				ID:           "user-id",
				Name:         "テストユーザー",
				Username:     "test-user",
				Email:        "test@calmato.com",
				ThumbnailURL: "",
				Language:     "English",
				CreatedAt:    current,
				UpdatedAt:    current,
			},
			Expected: &user.User{
				ID:           "user-id",
				Name:         "テストユーザー",
				Username:     "test-user",
				Email:        "test@calmato.com",
				ThumbnailURL: "",
				Language:     "English",
				CreatedAt:    current,
				UpdatedAt:    current,
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

		// Defined mocks
		urvm := mock_validation.NewMockUserRequestValidation(ctrl)
		urvm.EXPECT().UpdatePassword(testCase.Request).Return(ves)

		usm := mock_user.NewMockUserService(ctrl)
		usm.EXPECT().Authentication(ctx).Return(testCase.Expected, nil)
		usm.EXPECT().UpdatePassword(ctx, testCase.Auth.ID, testCase.Request.Password).Return(nil)

		// Start test
		t.Run(result, func(t *testing.T) {
			target := NewUserApplication(urvm, usm)

			got, err := target.UpdatePassword(ctx, testCase.Request)
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
