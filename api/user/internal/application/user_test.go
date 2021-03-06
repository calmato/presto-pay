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
	"golang.org/x/xerrors"
)

func TestUserApplication_IndexByUsername(t *testing.T) {
	testCases := map[string]struct {
		Request  *request.IndexByUsername
		Expected []*user.User
	}{
		"ok": {
			Request: &request.IndexByUsername{
				Username: "test-user",
				StartAt:  "",
			},
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

		// Defined variables
		ves := make([]*domain.ValidationError, 0)

		u := &user.User{}

		// Defined mocks
		urvm := mock_validation.NewMockUserRequestValidation(ctrl)
		urvm.EXPECT().IndexByUsername(testCase.Request).Return(ves)

		usm := mock_user.NewMockUserService(ctrl)
		usm.EXPECT().Authentication(ctx).Return(u, nil)
		usm.EXPECT().IndexByUsername(ctx, testCase.Request.Username, testCase.Request.StartAt).
			Return(testCase.Expected, nil)

		// Start test
		t.Run(result, func(t *testing.T) {
			target := NewUserApplication(urvm, usm)

			got, err := target.IndexByUsername(ctx, testCase.Request)
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

func TestUserApplication_IndexFriends(t *testing.T) {
	testCases := map[string]struct {
		Expected []*user.User
	}{
		"ok": {
			Expected: []*user.User{
				{
					ID:           "user-id",
					Name:         "テストユーザー",
					Username:     "test-user",
					Email:        "test@calmato.com",
					GroupIDs:     []string{"group-id"},
					FriendIDs:    []string{},
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

		// Defined variables
		u := &user.User{}

		// Defined mocks
		urvm := mock_validation.NewMockUserRequestValidation(ctrl)

		usm := mock_user.NewMockUserService(ctrl)
		usm.EXPECT().Authentication(ctx).Return(u, nil)
		usm.EXPECT().IndexFriends(ctx, u).Return(testCase.Expected, nil)

		// Start test
		t.Run(result, func(t *testing.T) {
			target := NewUserApplication(urvm, usm)

			got, err := target.IndexFriends(ctx)
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

func TestUserApplication_Show(t *testing.T) {
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

		// Defined variables
		u := &user.User{}

		// Defined mocks
		urvm := mock_validation.NewMockUserRequestValidation(ctrl)

		usm := mock_user.NewMockUserService(ctrl)
		usm.EXPECT().Authentication(ctx).Return(u, nil)
		usm.EXPECT().Show(ctx, testCase.UserID).Return(testCase.Expected, nil)

		// Start test
		t.Run(result, func(t *testing.T) {
			target := NewUserApplication(urvm, usm)

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

func TestUserApplication_ShowProfile(t *testing.T) {
	testCases := map[string]struct{}{
		"ok": {},
	}

	for result := range testCases {
		ctx, cancel := context.WithCancel(context.Background())
		defer cancel()

		ctrl := gomock.NewController(t)
		defer ctrl.Finish()

		// Defined variables
		u := &user.User{}

		// Defined mocks
		urvm := mock_validation.NewMockUserRequestValidation(ctrl)

		usm := mock_user.NewMockUserService(ctrl)
		usm.EXPECT().Authentication(ctx).Return(u, nil)

		// Start test
		t.Run(result, func(t *testing.T) {
			target := NewUserApplication(urvm, usm)

			_, err := target.ShowProfile(ctx)
			if err != nil {
				t.Fatalf("error: %v", err)
				return
			}
		})
	}
}

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

func TestUserApplication_RegisterInstanceID(t *testing.T) {
	testCases := map[string]struct {
		Request *request.RegisterInstanceID
	}{
		"ok": {
			Request: &request.RegisterInstanceID{
				InstanceID: "instance-id",
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

		u := &user.User{}

		// Defined mocks
		urvm := mock_validation.NewMockUserRequestValidation(ctrl)
		urvm.EXPECT().RegisterInstanceID(testCase.Request).Return(ves)

		usm := mock_user.NewMockUserService(ctrl)
		usm.EXPECT().Authentication(ctx).Return(u, nil)
		usm.EXPECT().Update(ctx, u).Return(u, nil)

		// Start test
		t.Run(result, func(t *testing.T) {
			target := NewUserApplication(urvm, usm)

			_, err := target.RegisterInstanceID(ctx, testCase.Request)
			if err != nil {
				t.Fatalf("error: %v", err)
				return
			}
		})
	}
}

func TestUserApplication_UpdateProfile(t *testing.T) {
	testCases := map[string]struct {
		Request *request.UpdateProfile
	}{
		"ok": {
			Request: &request.UpdateProfile{
				Name:      "テストユーザー",
				Username:  "test-user",
				Email:     "test@calmato.com",
				Thumbnail: "",
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
		}

		// Defined mocks
		urvm := mock_validation.NewMockUserRequestValidation(ctrl)
		urvm.EXPECT().UpdateProfile(testCase.Request).Return(ves)

		usm := mock_user.NewMockUserService(ctrl)
		usm.EXPECT().Authentication(ctx).Return(u, nil)
		usm.EXPECT().Update(ctx, u).Return(u, nil)

		// Start test
		t.Run(result, func(t *testing.T) {
			target := NewUserApplication(urvm, usm)

			_, err := target.UpdateProfile(ctx, testCase.Request)
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
				CreatedAt:    current,
				UpdatedAt:    current,
			},
			Expected: &user.User{
				ID:           "user-id",
				Name:         "テストユーザー",
				Username:     "test-user",
				Email:        "test@calmato.com",
				ThumbnailURL: "",
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

func TestUserApplication_UniqueCheckEmail(t *testing.T) {
	testCases := map[string]struct {
		AuthUser    *user.User
		Request     *request.UniqueCheckUserEmail
		UniqueCheck bool
		Expected    bool
	}{
		"ok": {
			AuthUser: &user.User{
				ID:           "user-id",
				Name:         "テストユーザー",
				Username:     "test-user",
				Email:        "test@calmato.com",
				ThumbnailURL: "",
			},
			Request: &request.UniqueCheckUserEmail{
				Email: "test@calmato.com",
			},
			UniqueCheck: true,
			Expected:    true,
		},
		"ng": {
			AuthUser: nil,
			Request: &request.UniqueCheckUserEmail{
				Email: "test@calmato.com",
			},
			UniqueCheck: false,
			Expected:    false,
		},
	}

	for result, testCase := range testCases {
		ctx, cancel := context.WithCancel(context.Background())
		defer cancel()

		ctrl := gomock.NewController(t)
		defer ctrl.Finish()

		// Defined variables
		ves := make([]*domain.ValidationError, 0)
		if testCase.Request.Email == "" {
			ves = append(ves, &domain.ValidationError{})
		}

		authErr := xerrors.New("some errors")
		if testCase.AuthUser != nil {
			authErr = nil
		}

		// Defined mocks
		urvm := mock_validation.NewMockUserRequestValidation(ctrl)
		urvm.EXPECT().UniqueCheckEmail(testCase.Request).Return(ves)

		usm := mock_user.NewMockUserService(ctrl)
		usm.EXPECT().Authentication(ctx).Return(testCase.AuthUser, authErr)
		usm.EXPECT().UniqueCheckEmail(ctx, testCase.AuthUser, testCase.Request.Email).Return(testCase.UniqueCheck)

		// Start test
		t.Run(result, func(t *testing.T) {
			target := NewUserApplication(urvm, usm)

			got, err := target.UniqueCheckEmail(ctx, testCase.Request)
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

func TestUserApplication_UniqueCheckUsername(t *testing.T) {
	testCases := map[string]struct {
		AuthUser    *user.User
		Request     *request.UniqueCheckUserUsername
		UniqueCheck bool
		Expected    bool
	}{
		"ok": {
			AuthUser: &user.User{
				ID:           "user-id",
				Name:         "テストユーザー",
				Username:     "test-user",
				Email:        "test@calmato.com",
				ThumbnailURL: "",
			},
			Request: &request.UniqueCheckUserUsername{
				Username: "test-user",
			},
			UniqueCheck: true,
			Expected:    true,
		},
		"ng": {
			AuthUser: nil,
			Request: &request.UniqueCheckUserUsername{
				Username: "test-user",
			},
			UniqueCheck: false,
			Expected:    false,
		},
	}

	for result, testCase := range testCases {
		ctx, cancel := context.WithCancel(context.Background())
		defer cancel()

		ctrl := gomock.NewController(t)
		defer ctrl.Finish()

		// Defined variables
		ves := make([]*domain.ValidationError, 0)
		if testCase.Request.Username == "" {
			ves = append(ves, &domain.ValidationError{})
		}

		authErr := xerrors.New("some errors")
		if testCase.AuthUser != nil {
			authErr = nil
		}

		// Defined mocks
		urvm := mock_validation.NewMockUserRequestValidation(ctrl)
		urvm.EXPECT().UniqueCheckUsername(testCase.Request).Return(ves)

		usm := mock_user.NewMockUserService(ctrl)
		usm.EXPECT().Authentication(ctx).Return(testCase.AuthUser, authErr)
		usm.EXPECT().UniqueCheckUsername(ctx, testCase.AuthUser, testCase.Request.Username).Return(testCase.UniqueCheck)

		// Start test
		t.Run(result, func(t *testing.T) {
			target := NewUserApplication(urvm, usm)

			got, err := target.UniqueCheckUsername(ctx, testCase.Request)
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

func TestUserApplication_AddGroup(t *testing.T) {
	current := time.Now()

	testCases := map[string]struct {
		UserID   string
		GroupID  string
		Expected *user.User
	}{
		"ok": {
			UserID:  "user-id",
			GroupID: "group-id",
			Expected: &user.User{
				ID:           "user-id",
				Name:         "テストユーザー",
				Username:     "test-user",
				Email:        "test@calmato.com",
				ThumbnailURL: "",
				GroupIDs:     []string{},
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
		u := &user.User{}

		// Defined mocks
		urvm := mock_validation.NewMockUserRequestValidation(ctrl)

		usm := mock_user.NewMockUserService(ctrl)
		usm.EXPECT().Authentication(ctx).Return(u, nil)
		usm.EXPECT().Show(ctx, testCase.UserID).Return(testCase.Expected, nil)
		usm.EXPECT().Update(ctx, testCase.Expected).Return(testCase.Expected, nil)
		usm.EXPECT().ContainsGroupID(ctx, testCase.Expected, testCase.GroupID).Return(false, nil)

		// Start test
		t.Run(result, func(t *testing.T) {
			target := NewUserApplication(urvm, usm)

			got, err := target.AddGroup(ctx, testCase.UserID, testCase.GroupID)
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

func TestUserApplication_RemoveGroup(t *testing.T) {
	current := time.Now()

	testCases := map[string]struct {
		UserID   string
		GroupID  string
		Expected *user.User
	}{
		"ok": {
			UserID:  "user-id",
			GroupID: "group-id",
			Expected: &user.User{
				ID:           "user-id",
				Name:         "テストユーザー",
				Username:     "test-user",
				Email:        "test@calmato.com",
				ThumbnailURL: "",
				GroupIDs:     []string{"group-id"},
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
		u := &user.User{}

		// Defined mocks
		urvm := mock_validation.NewMockUserRequestValidation(ctrl)

		usm := mock_user.NewMockUserService(ctrl)
		usm.EXPECT().Authentication(ctx).Return(u, nil)
		usm.EXPECT().Show(ctx, testCase.UserID).Return(testCase.Expected, nil)
		usm.EXPECT().Update(ctx, testCase.Expected).Return(testCase.Expected, nil)
		usm.EXPECT().ContainsGroupID(ctx, testCase.Expected, testCase.GroupID).Return(true, nil)

		// Start test
		t.Run(result, func(t *testing.T) {
			target := NewUserApplication(urvm, usm)

			got, err := target.RemoveGroup(ctx, testCase.UserID, testCase.GroupID)
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

func TestUserApplication_AddFriend(t *testing.T) {
	current := time.Now()

	testCases := map[string]struct {
		Request *request.AddFriend
		User    *user.User
	}{
		"ok": {
			Request: &request.AddFriend{
				UserID: "friend-id",
			},
			User: &user.User{
				ID:           "friend-id",
				Name:         "テストユーザー",
				Username:     "test-user",
				Email:        "test@calmato.com",
				ThumbnailURL: "",
				GroupIDs:     []string{},
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

		u := &user.User{}

		// Defined mocks
		urvm := mock_validation.NewMockUserRequestValidation(ctrl)
		urvm.EXPECT().AddFriend(testCase.Request).Return(ves)

		usm := mock_user.NewMockUserService(ctrl)
		usm.EXPECT().Authentication(ctx).Return(u, nil)
		usm.EXPECT().Show(ctx, testCase.Request.UserID).Return(testCase.User, nil)
		usm.EXPECT().Update(ctx, u).Return(u, nil)
		usm.EXPECT().ContainsFriendID(ctx, u, testCase.Request.UserID).Return(false, nil)

		// Start test
		t.Run(result, func(t *testing.T) {
			target := NewUserApplication(urvm, usm)

			_, err := target.AddFriend(ctx, testCase.Request)
			if err != nil {
				t.Fatalf("error: %v", err)
				return
			}
		})
	}
}

func TestUserApplication_RemoveFriend(t *testing.T) {
	current := time.Now()

	testCases := map[string]struct {
		UserID string
		User   *user.User
	}{
		"ok": {
			UserID: "friend-id",
			User: &user.User{
				ID:           "friend-id",
				Name:         "テストユーザー",
				Username:     "test-user",
				Email:        "test@calmato.com",
				ThumbnailURL: "",
				GroupIDs:     []string{},
				FriendIDs:    []string{"friend-id"},
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
		u := &user.User{
			FriendIDs: testCase.User.FriendIDs,
		}

		// Defined mocks
		urvm := mock_validation.NewMockUserRequestValidation(ctrl)

		usm := mock_user.NewMockUserService(ctrl)
		usm.EXPECT().Authentication(ctx).Return(u, nil)
		usm.EXPECT().Update(ctx, u).Return(u, nil)
		usm.EXPECT().ContainsFriendID(ctx, u, testCase.UserID).Return(true, nil)

		// Start test
		t.Run(result, func(t *testing.T) {
			target := NewUserApplication(urvm, usm)

			_, err := target.RemoveFriend(ctx, testCase.UserID)
			if err != nil {
				t.Fatalf("error: %v", err)
				return
			}
		})
	}
}
