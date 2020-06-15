package validation

import (
	"context"
	"reflect"
	"testing"
	"time"

	"github.com/calmato/presto-pay/api/calc/internal/domain"
	"github.com/calmato/presto-pay/api/calc/internal/domain/group"
	"github.com/calmato/presto-pay/api/calc/internal/domain/user"
	mock_group "github.com/calmato/presto-pay/api/calc/mock/domain/group"
	mock_api "github.com/calmato/presto-pay/api/calc/mock/infrastructure/api"
	"github.com/golang/mock/gomock"
)

func TestGroupValidation_Group(t *testing.T) {
	current := time.Now()

	testCases := map[string]struct {
		Group    *group.Group
		Expected []*domain.ValidationError
	}{
		"ok": {
			Group: &group.Group{
				ID:           "group-id",
				Name:         "テストグループ",
				ThumbnailURL: "",
				UserIDs:      []string{"user-id"},
				CreatedAt:    current,
				UpdatedAt:    current,
			},
			Expected: make([]*domain.ValidationError, 0),
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
		grm := mock_group.NewMockGroupRepository(ctrl)

		acm := mock_api.NewMockAPIClient(ctrl)
		acm.EXPECT().UserExists(ctx, u.ID).Return(true, nil)

		t.Run(result, func(t *testing.T) {
			target := NewGroupDomainValidation(grm, acm)

			got := target.Group(ctx, testCase.Group)
			if !reflect.DeepEqual(got, testCase.Expected) {
				t.Fatalf("want %#v, but %#v", testCase.Expected, got)
				return
			}
		})
	}
}
