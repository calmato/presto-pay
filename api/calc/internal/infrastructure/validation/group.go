package validation

import (
	"context"

	"github.com/calmato/presto-pay/api/calc/internal/domain"
	"github.com/calmato/presto-pay/api/calc/internal/domain/group"
	"github.com/calmato/presto-pay/api/calc/internal/infrastructure/api"
	"golang.org/x/xerrors"
)

type groupDomainValidation struct {
	groupRepository group.GroupRepository
	apiClient       api.APIClient
}

// NewGroupDomainValidation - GroupDomainValidationの生成
func NewGroupDomainValidation(gr group.GroupRepository, ac api.APIClient) group.GroupDomainValidation {
	return &groupDomainValidation{
		groupRepository: gr,
		apiClient:       ac,
	}
}

func (gdv *groupDomainValidation) Group(ctx context.Context, g *group.Group) []*domain.ValidationError {
	ves := make([]*domain.ValidationError, 0)

	if err := uniqueCheckUserIDs(g.UserIDs); err != nil {
		ve := &domain.ValidationError{
			Field:   "userIds",
			Message: domain.CustomUniqueMessage,
		}

		ves = append(ves, ve)
	}

	for _, userID := range g.UserIDs {
		if !userIDExists(ctx, gdv.apiClient, userID) {
			ve := &domain.ValidationError{
				Field:   "userIds",
				Message: domain.CustomNotExistsMessage,
			}

			ves = append(ves, ve)
			break
		}
	}

	return ves
}

func uniqueCheckUserIDs(userIDs []string) error {
	m := make(map[string]struct{})

	for _, v := range userIDs {
		if _, ok := m[v]; ok {
			return xerrors.New("There are duplicate values.")
		}

		m[v] = struct{}{}
	}

	return nil
}

func uniqueCheckTags(tags []string) error {
	m := make(map[string]struct{})

	for _, v := range tags {
		if _, ok := m[v]; ok {
			return xerrors.New("There are duplicate values.")
		}

		m[v] = struct{}{}
	}

	return nil
}
