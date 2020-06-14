package application

import (
	"context"
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
