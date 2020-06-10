package group

import "context"

// GroupRepository - GroupRepositoryインターフェース
type GroupRepository interface {
	Create(ctx context.Context, g *Group) error
}
