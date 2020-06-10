package group

import (
	"context"

	"github.com/calmato/presto-pay/api/calc/internal/domain"
)

// GroupDomainValidation - GroupDomainValidationインターフェース
type GroupDomainValidation interface {
	Group(ctx context.Context, g *Group) []*domain.ValidationError
}
