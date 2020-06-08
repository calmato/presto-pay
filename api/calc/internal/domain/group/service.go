package group

import "context"

// GroupService - GroupServiceインターフェース
type GroupService interface {
	Create(ctx context.Context, g *Group) (*Group, error)
	UploadThumbnail(ctx context.Context, data []byte) (string, error)
}
