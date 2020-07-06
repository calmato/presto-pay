package service

import (
	"context"
	"reflect"
	"testing"
	"time"

	"github.com/calmato/presto-pay/api/calc/internal/domain/user"
	mock_api "github.com/calmato/presto-pay/api/calc/mock/infrastructure/api"
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
		acm := mock_api.NewMockAPIClient(ctrl)
		acm.EXPECT().Authentication(ctx).Return(testCase.Expected, nil)

		// Start test
		t.Run(result, func(t *testing.T) {
			target := NewUserService(acm)

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

func TestUserService_ContainsGroupID(t *testing.T) {
	testCases := map[string]struct {
		User     *user.User
		GroupID  string
		Expected bool
	}{
		"ok": {
			User: &user.User{
				ID:           "user-id",
				Name:         "テストユーザー",
				Username:     "test-user",
				Email:        "test@calmato.com",
				GroupIDs:     []string{"group-id"},
				ThumbnailURL: "",
			},
			GroupID:  "group-id",
			Expected: true,
		},
		"ng": {
			User: &user.User{
				ID:           "user-id",
				Name:         "テストユーザー",
				Username:     "test-user",
				Email:        "test@calmato.com",
				GroupIDs:     []string{},
				ThumbnailURL: "",
			},
			GroupID:  "group-id",
			Expected: false,
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

		// Start test
		t.Run(result, func(t *testing.T) {
			target := NewUserService(udvm, urm, uum)

			got, err := target.ContainsGroupID(ctx, testCase.User, testCase.GroupID)
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
