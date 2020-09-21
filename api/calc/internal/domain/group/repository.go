package group

import "context"

// GroupRepository - GroupRepositoryインターフェース
type GroupRepository interface {
	Show(ctx context.Context, groupID string) (*Group, error)
	Create(ctx context.Context, g *Group) error
	Update(ctx context.Context, g *Group) error
	Destroy(ctx context.Context, groupID string) error
}
