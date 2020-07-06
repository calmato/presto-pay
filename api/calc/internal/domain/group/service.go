package group

import (
	"context"

	"github.com/calmato/presto-pay/api/calc/internal/domain/user"
)

// GroupService - GroupServiceインターフェース
type GroupService interface {
	IndexJoinGroups(ctx context.Context, u *user.User) ([]*Group, error)
	Show(ctx context.Context, groupID string) (*Group, error)
	Create(ctx context.Context, g *Group) (*Group, error)
	UploadThumbnail(ctx context.Context, data []byte) (string, error)
	ContainsUserID(ctx context.Context, g *Group, userID string) (bool, error)
}
