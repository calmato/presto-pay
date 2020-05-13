package user

import "context"

// UserService - UserServiceインターフェース
type UserService interface {
	Create(ctx context.Context, u *User) (*User, error)
	UploadThumbnail(ctx context.Context, data []byte) (string, error)
}
