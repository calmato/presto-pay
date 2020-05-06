package user

import "context"

type UserService interface {
	Create(ctx context.Context, u *User) error
	UploadThumbnail(ctx context.Context, data []byte) (string, error)
}
