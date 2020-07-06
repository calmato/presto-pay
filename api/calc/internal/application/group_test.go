package application

import (
	"context"
	"reflect"
	"testing"

	"github.com/calmato/presto-pay/api/calc/internal/application/request"
	"github.com/calmato/presto-pay/api/calc/internal/domain"
	"github.com/calmato/presto-pay/api/calc/internal/domain/group"
	"github.com/calmato/presto-pay/api/calc/internal/domain/user"
	mock_validation "github.com/calmato/presto-pay/api/calc/mock/application/validation"
	mock_group "github.com/calmato/presto-pay/api/calc/mock/domain/group"
	mock_user "github.com/calmato/presto-pay/api/calc/mock/domain/user"
	"github.com/golang/mock/gomock"
)

// TODO: そのうちちゃんとしたテスト書く
func TestGroupApplication_Index(t *testing.T) {
	testCases := map[string]struct {
		Expected []*group.Group
	}{
		"ok": {
			Expected: []*group.Group{},
		},
	}

	for result, testCase := range testCases {
		ctx, cancel := context.WithCancel(context.Background())
		defer cancel()

		ctrl := gomock.NewController(t)
		defer ctrl.Finish()

		// Defined variables
		u := &user.User{
			ID: "user-id",
		}

		// Defined mocks
		grvm := mock_validation.NewMockGroupRequestValidation(ctrl)

		usm := mock_user.NewMockUserService(ctrl)
		usm.EXPECT().Authentication(ctx).Return(u, nil)

		gsm := mock_group.NewMockGroupService(ctrl)
		gsm.EXPECT().IndexJoinGroups(ctx, u).Return(testCase.Expected, nil)

		// Start test
		t.Run(result, func(t *testing.T) {
			target := NewGroupApplication(grvm, usm, gsm)

			got, err := target.Index(ctx)
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

func TestGroupApplication_Show(t *testing.T) {
	testCases := map[string]struct {
		GroupID  string
		Expected *group.Group
	}{
		"ok": {
			GroupID: "group-id",
			Expected: *group.Group{
				ID:           "group-id",
				Name:         "テストグループ",
				ThumbnailURL: "",
				UserIDs:      []string{},
				Users:        []*user.User{},
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
			ID:       "user-id",
			GroupIDs: []string{testCase.GroupID},
		}

		// Defined mocks
		grvm := mock_validation.NewMockGroupRequestValidation(ctrl)

		usm := mock_user.NewMockUserService(ctrl)
		usm.EXPECT().Authentication(ctx).Return(u, nil)

		gsm := mock_group.NewMockGroupService(ctrl)
		gsm.EXPECT().Show(ctx, testCase.GroupID).Return(testCase.Expected, nil)

		// Start test
		t.Run(result, func(t *testing.T) {
			target := NewGroupApplication(grvm, usm, gsm)

			got, err := target.Show(ctx)
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

func TestGroupApplication_Create(t *testing.T) {
	testCases := map[string]struct {
		Request *request.CreateGroup
	}{
		"ok": {
			Request: &request.CreateGroup{
				Name:      "テストユーザー",
				Thumbnail: "",
				UserIDs:   []string{},
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
			ID: "user-id",
		}

		g := &group.Group{
			Name:         testCase.Request.Name,
			ThumbnailURL: "",
			UserIDs:      testCase.Request.UserIDs,
		}

		// Defined mocks
		grvm := mock_validation.NewMockGroupRequestValidation(ctrl)
		grvm.EXPECT().CreateGroup(testCase.Request).Return(ves)

		usm := mock_user.NewMockUserService(ctrl)
		usm.EXPECT().Authentication(ctx).Return(u, nil)

		gsm := mock_group.NewMockGroupService(ctrl)
		gsm.EXPECT().Create(ctx, g).Return(g, nil)
		gsm.EXPECT().ContainsUserID(ctx, g, u.ID).Return(true, nil)

		// Start test
		t.Run(result, func(t *testing.T) {
			target := NewGroupApplication(grvm, usm, gsm)

			_, err := target.Create(ctx, testCase.Request)
			if err != nil {
				t.Fatalf("error: %v", err)
				return
			}
		})
	}
}
