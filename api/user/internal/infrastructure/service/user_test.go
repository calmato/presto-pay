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

func TestUserService_IndexByUsername(t *testing.T) {
	testCases := map[string]struct {
		Username string
		StartAt  string
		Expected []*user.User
	}{
		"ok01": {
			Username: "test",
			StartAt:  "",
			Expected: []*user.User{
				{
					ID:           "user-id",
					Name:         "テストユーザー",
					Username:     "test-user",
					Email:        "test@calmato.com",
					GroupIDs:     []string{"group-id"},
					ThumbnailURL: "",
				},
			},
		},
		"ok02": {
			Username: "test",
			StartAt:  "test",
			Expected: []*user.User{
				{
					ID:           "user-id",
					Name:         "テストユーザー",
					Username:     "test-user",
					Email:        "test@calmato.com",
					GroupIDs:     []string{"group-id"},
					ThumbnailURL: "",
				},
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
		if testCase.StartAt == "" {
			urm.EXPECT().IndexByUsername(ctx, testCase.Username).Return(testCase.Expected, nil)
		} else {
			urm.EXPECT().
				IndexByUsernameFromStartAt(ctx, testCase.Username, testCase.StartAt).
				Return(testCase.Expected, nil)
		}

		uum := mock_user.NewMockUserUploader(ctrl)

		// Start test
		t.Run(result, func(t *testing.T) {
			target := NewUserService(udvm, urm, uum)

			got, err := target.IndexByUsername(ctx, testCase.Username, testCase.StartAt)
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

func TestUserService_Show(t *testing.T) {
	testCases := map[string]struct {
		UserID   string
		Expected *user.User
	}{
		"ok": {
			UserID: "user-id",
			Expected: &user.User{
				ID:           "user-id",
				Name:         "テストユーザー",
				Username:     "test-user",
				Email:        "test@calmato.com",
				GroupIDs:     []string{"group-id"},
				ThumbnailURL: "",
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
		urm.EXPECT().ShowByUserID(ctx, testCase.UserID).Return(testCase.Expected, nil)

		uum := mock_user.NewMockUserUploader(ctrl)

		// Start test
		t.Run(result, func(t *testing.T) {
			target := NewUserService(udvm, urm, uum)

			got, err := target.Show(ctx, testCase.UserID)
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

func TestUserService_UpdatePassword(t *testing.T) {
	testCases := map[string]struct {
		ID       string
		Password string
	}{
		"ok": {
			ID:       "user-id",
			Password: "!Qaz2wsx",
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
		urm.EXPECT().UpdatePassword(ctx, testCase.ID, testCase.Password).Return(nil)

		uum := mock_user.NewMockUserUploader(ctrl)

		// Start test
		t.Run(result, func(t *testing.T) {
			target := NewUserService(udvm, urm, uum)

			err := target.UpdatePassword(ctx, testCase.ID, testCase.Password)
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

func TestUserService_UniqueCheckEmail(t *testing.T) {
	testCases := map[string]struct {
		AuthUser           *user.User
		Email              string
		RepositoryResponse string
		Expected           bool
	}{
		"ok01": {
			AuthUser: &user.User{
				ID:           "user-id",
				Name:         "テストユーザー",
				Username:     "test-user",
				Email:        "test@calmato.com",
				ThumbnailURL: "",
			},
			Email:              "test@calmato.com",
			RepositoryResponse: "user-id",
			Expected:           true,
		},
		"ok02": {
			AuthUser: &user.User{
				ID:           "user-id",
				Name:         "テストユーザー",
				Username:     "test-user",
				Email:        "test@calmato.com",
				ThumbnailURL: "",
			},
			Email:              "aiueo@calmato.com",
			RepositoryResponse: "",
			Expected:           true,
		},
		"ng01": {
			AuthUser:           &user.User{},
			Email:              "aiueo@calmato.com",
			RepositoryResponse: "aiueo-id",
			Expected:           false,
		},
		"ng02": {
			AuthUser: &user.User{
				ID:           "user-id",
				Name:         "テストユーザー",
				Username:     "test-user",
				Email:        "test@calmato.com",
				ThumbnailURL: "",
			},
			Email:              "aiueo@calmato.com",
			RepositoryResponse: "aiueo-id",
			Expected:           false,
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
		urm.EXPECT().GetUIDByEmail(ctx, testCase.Email).Return(testCase.RepositoryResponse, nil)

		uum := mock_user.NewMockUserUploader(ctrl)

		// Start test
		t.Run(result, func(t *testing.T) {
			target := NewUserService(udvm, urm, uum)

			got := target.UniqueCheckEmail(ctx, testCase.AuthUser, testCase.Email)
			if !reflect.DeepEqual(got, testCase.Expected) {
				t.Fatalf("want %#v, but %#v", testCase.Expected, got)
				return
			}
		})
	}
}

func TestUserService_UniqueCheckUsername(t *testing.T) {
	testCases := map[string]struct {
		AuthUser           *user.User
		Username           string
		RepositoryResponse *user.User
		Expected           bool
	}{
		"ok01": {
			AuthUser: &user.User{
				ID:           "user-id",
				Name:         "テストユーザー",
				Username:     "test-user",
				Email:        "test@calmato.com",
				ThumbnailURL: "",
			},
			Username: "test-user",
			RepositoryResponse: &user.User{
				ID:           "user-id",
				Name:         "テストユーザー",
				Username:     "test-user",
				Email:        "test@calmato.com",
				ThumbnailURL: "",
			},
			Expected: true,
		},
		"ok02": {
			AuthUser: &user.User{
				ID:           "user-id",
				Name:         "テストユーザー",
				Username:     "test-user",
				Email:        "test@calmato.com",
				ThumbnailURL: "",
			},
			Username:           "aiueo-user",
			RepositoryResponse: nil,
			Expected:           true,
		},
		"ng01": {
			AuthUser: &user.User{},
			Username: "aiueo-user",
			RepositoryResponse: &user.User{
				ID:           "aiueo-id",
				Name:         "あいうえお",
				Username:     "aiueo-user",
				Email:        "aiueo@calmato.com",
				ThumbnailURL: "",
			},
			Expected: false,
		},
		"ng02": {
			AuthUser: &user.User{
				ID:           "user-id",
				Name:         "テストユーザー",
				Username:     "test-user",
				Email:        "test@calmato.com",
				ThumbnailURL: "",
			},
			Username: "aiueo-user",
			RepositoryResponse: &user.User{
				ID:           "aiueo-id",
				Name:         "あいうえお",
				Username:     "aiueo-user",
				Email:        "aiueo@calmato.com",
				ThumbnailURL: "",
			},
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
		urm.EXPECT().ShowByUsername(ctx, testCase.Username).Return(testCase.RepositoryResponse, nil)

		uum := mock_user.NewMockUserUploader(ctrl)

		// Start test
		t.Run(result, func(t *testing.T) {
			target := NewUserService(udvm, urm, uum)

			got := target.UniqueCheckUsername(ctx, testCase.AuthUser, testCase.Username)
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

func TestUserService_ContainsFriendID(t *testing.T) {
	testCases := map[string]struct {
		User     *user.User
		FriendID string
		Expected bool
	}{
		"ok": {
			User: &user.User{
				ID:           "user-id",
				Name:         "テストユーザー",
				Username:     "test-user",
				Email:        "test@calmato.com",
				GroupIDs:     []string{"group-id"},
				FriendIDs:    []string{"friend-id"},
				ThumbnailURL: "",
			},
			FriendID: "friend-id",
			Expected: true,
		},
		"ng": {
			User: &user.User{
				ID:           "user-id",
				Name:         "テストユーザー",
				Username:     "test-user",
				Email:        "test@calmato.com",
				GroupIDs:     []string{},
				FriendIDs:    []string{},
				ThumbnailURL: "",
			},
			FriendID: "friend-id",
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

			got, err := target.ContainsFriendID(ctx, testCase.User, testCase.FriendID)
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
