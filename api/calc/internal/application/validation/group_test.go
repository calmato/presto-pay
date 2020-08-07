package validation

import (
	"reflect"
	"testing"

	"github.com/calmato/presto-pay/api/calc/internal/application/request"
	"github.com/calmato/presto-pay/api/calc/internal/domain"
)

func TestGroupRequestValidation_CreateGroup(t *testing.T) {
	testCases := map[string]struct {
		Request  *request.CreateGroup
		Expected []*domain.ValidationError
	}{
		"ok": {
			Request: &request.CreateGroup{
				Name:      "テストユーザー",
				Thumbnail: "",
				UserIDs:   []string{},
			},
			Expected: make([]*domain.ValidationError, 0),
		},
	}

	for result, testCase := range testCases {
		// Start test
		t.Run(result, func(t *testing.T) {
			target := NewGroupRequestValidation()

			got := target.CreateGroup(testCase.Request)
			if !reflect.DeepEqual(got, testCase.Expected) {
				t.Fatalf("want %#v, but %#v", testCase.Expected, got)
				return
			}
		})
	}
}

func TestGroupRequestValidation_AddUsersInGroup(t *testing.T) {
	testCases := map[string]struct {
		Request  *request.AddUsersInGroup
		Expected []*domain.ValidationError
	}{
		"ok": {
			Request: &request.AddUsersInGroup{
				UserIDs: []string{},
			},
			Expected: make([]*domain.ValidationError, 0),
		},
	}

	for result, testCase := range testCases {
		// Start test
		t.Run(result, func(t *testing.T) {
			target := NewGroupRequestValidation()

			got := target.AddUsersInGroup(testCase.Request)
			if !reflect.DeepEqual(got, testCase.Expected) {
				t.Fatalf("want %#v, but %#v", testCase.Expected, got)
				return
			}
		})
	}
}
