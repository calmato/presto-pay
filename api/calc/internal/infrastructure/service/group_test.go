package service

import (
	"context"
	"reflect"
	"testing"

	"github.com/calmato/presto-pay/api/calc/internal/domain/group"
	"github.com/calmato/presto-pay/api/calc/internal/domain/user"
	mock_group "github.com/calmato/presto-pay/api/calc/mock/domain/group"
	mock_api "github.com/calmato/presto-pay/api/calc/mock/infrastructure/api"
	"github.com/golang/mock/gomock"
)

func TestGroupService_Create(t *testing.T) {
	testCases := map[string]struct {
		Group *group.Group
	}{
		"ok": {
			Group: &group.Group{
				ID:           "group-id",
				Name:         "テストグループ",
				ThumbnailURL: "",
				UserIDs:      []string{"user-id"},
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
			ID: testCase.Group.UserIDs[0],
		}

		// Defined mocks
		gdvm := mock_group.NewMockGroupDomainValidation(ctrl)
		gdvm.EXPECT().Group(ctx, testCase.Group).Return(nil)

		grm := mock_group.NewMockGroupRepository(ctrl)
		grm.EXPECT().Create(ctx, testCase.Group).Return(nil)

		gum := mock_group.NewMockGroupUploader(ctrl)

		acm := mock_api.NewMockAPIClient(ctrl)
		acm.EXPECT().AddGroup(ctx, u.ID, gomock.Any()).Return(nil)

		// Start test
		t.Run(result, func(t *testing.T) {
			target := NewGroupService(gdvm, grm, gum, acm)

			_, err := target.Create(ctx, testCase.Group)
			if err != nil {
				t.Fatalf("error: %v", err)
				return
			}
		})
	}
}

func TestGroupService_UploadThumbnail(t *testing.T) {
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
		gdvm := mock_group.NewMockGroupDomainValidation(ctrl)

		grm := mock_group.NewMockGroupRepository(ctrl)

		gum := mock_group.NewMockGroupUploader(ctrl)
		gum.EXPECT().UploadThumbnail(ctx, testCase.Data).Return(testCase.Expected, nil)

		acm := mock_api.NewMockAPIClient(ctrl)

		// Start test
		t.Run(result, func(t *testing.T) {
			target := NewGroupService(gdvm, grm, gum, acm)

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

func TestGroupService_ContainsUserID(t *testing.T) {
	testCases := map[string]struct {
		Group    *group.Group
		UserID   string
		Expected bool
	}{
		"ok": {
			Group: &group.Group{
				ID:           "group-id",
				Name:         "テストグループ",
				ThumbnailURL: "",
				UserIDs:      []string{"user-id"},
			},
			UserID:   "user-id",
			Expected: true,
		},
		"ng": {
			Group: &group.Group{
				ID:           "group-id",
				Name:         "テストグループ",
				ThumbnailURL: "",
				UserIDs:      []string{},
			},
			UserID:   "user-id",
			Expected: false,
		},
	}

	for result, testCase := range testCases {
		ctx, cancel := context.WithCancel(context.Background())
		defer cancel()

		ctrl := gomock.NewController(t)
		defer ctrl.Finish()

		// Defined mocks
		gdvm := mock_group.NewMockGroupDomainValidation(ctrl)

		grm := mock_group.NewMockGroupRepository(ctrl)

		gum := mock_group.NewMockGroupUploader(ctrl)

		acm := mock_api.NewMockAPIClient(ctrl)

		// Start test
		t.Run(result, func(t *testing.T) {
			target := NewGroupService(gdvm, grm, gum, acm)

			got, err := target.ContainsUserID(ctx, testCase.Group, testCase.UserID)
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