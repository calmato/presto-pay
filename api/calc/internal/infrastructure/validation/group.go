package validation

import (
	"context"

	"github.com/calmato/presto-pay/api/calc/internal/domain"
	"github.com/calmato/presto-pay/api/calc/internal/domain/group"
)

type groupDomainValidation struct {
	groupRepository group.GroupRepository
}

// NewGroupDomainValidation - GroupDomainValidationの生成
func NewGroupDomainValidation(gr group.GroupRepository) group.GroupDomainValidation {
	return &groupDomainValidation{
		groupRepository: gr,
	}
}

func (gdv *groupDomainValidation) Group(ctx context.Context, g *group.Group) []*domain.ValidationError {
	validationErrors := make([]*domain.ValidationError, 0)

	return validationErrors
}
