package service

import (
	"context"
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
