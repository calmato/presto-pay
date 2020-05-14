package service

import (
	"context"
	"reflect"
	"testing"
	"time"

	"github.com/calmato/presto-pay/api/user/internal/domain/user"
	mock_user "github.com/calmato/presto-pay/api/user/mock/domain/user"
	"github.com/golang/mock/gomock"
)

func TestUserService_Authentication(t *testing.T) {
	current := time.Now()

	testCases := map[string]struct {
		Expected *user.User
	}{
		"ok": {
			Expected: &user.User{
				ID:           "user-id",
				Name:         "テストユーザー",
				Username:     "test-user",
				Email:        "test@calmato.com",
				ThumbnailURL: "",
				Password:     "",
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

		// Defined mocks
		udvm := mock_user.NewMockUserDomainValidation(ctrl)

		urm := mock_user.NewMockUserRepository(ctrl)
		urm.EXPECT().Authentication(ctx).Return(testCase.Expected, nil)

		uum := mock_user.NewMockUserUploader(ctrl)

		// Start test
		t.Run(result, func(t *testing.T) {
			target := NewUserService(udvm, urm, uum)

			got, err := target.Authentication(ctx)
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

func TestUserService_Create(t *testing.T) {
	testCases := map[string]struct {
		User *user.User
	}{
		"ok": {
			User: &user.User{
				Name:         "テストユーザー",
				Username:     "test-user",
				Email:        "test@calmato.com",
				ThumbnailURL: "",
				Password:     "!Qaz2wsx",
			},
		},
	}

	for result, testCase := range testCases {
		ctx, cancel := context.WithCancel(context.Background())
		defer cancel()

		ctrl := gomock.NewController(t)
		defer ctrl.Finish()

		// Defined mocks
		udvm := mock_user.NewMockUserDomainValidation(ctrl)
		udvm.EXPECT().User(ctx, testCase.User).Return(nil)

		urm := mock_user.NewMockUserRepository(ctrl)
		urm.EXPECT().Create(ctx, testCase.User).Return(nil)

		uum := mock_user.NewMockUserUploader(ctrl)

		// Start test
		t.Run(result, func(t *testing.T) {
			target := NewUserService(udvm, urm, uum)

			_, err := target.Create(ctx, testCase.User)
			if err != nil {
				t.Fatalf("error: %v", err)
				return
			}
		})
	}
}

func TestUserService_Update(t *testing.T) {
	testCases := map[string]struct {
		User *user.User
	}{
		"ok": {
			User: &user.User{
				ID:           "user-id",
				Name:         "テストユーザー",
				Username:     "test-user",
				Email:        "test@calmato.com",
				ThumbnailURL: "",
				Language:     "English",
			},
		},
	}

	for result, testCase := range testCases {
		ctx, cancel := context.WithCancel(context.Background())
		defer cancel()

		ctrl := gomock.NewController(t)
		defer ctrl.Finish()

		// Defined mocks
		udvm := mock_user.NewMockUserDomainValidation(ctrl)
		udvm.EXPECT().User(ctx, testCase.User).Return(nil)

		urm := mock_user.NewMockUserRepository(ctrl)
		urm.EXPECT().Update(ctx, testCase.User).Return(nil)

		uum := mock_user.NewMockUserUploader(ctrl)

		// Start test
		t.Run(result, func(t *testing.T) {
			target := NewUserService(udvm, urm, uum)

			_, err := target.Update(ctx, testCase.User)
			if err != nil {
				t.Fatalf("error: %v", err)
				return
			}
		})
	}
}

func TestUserService_UploadThumbnail(t *testing.T) {
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
		udvm := mock_user.NewMockUserDomainValidation(ctrl)

		urm := mock_user.NewMockUserRepository(ctrl)

		uum := mock_user.NewMockUserUploader(ctrl)
		uum.EXPECT().UploadThumbnail(ctx, testCase.Data).Return(testCase.Expected, nil)

		// Start test
		t.Run(result, func(t *testing.T) {
			target := NewUserService(udvm, urm, uum)

			got, err := target.UploadThumbnail(ctx, testCase.Data)
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
