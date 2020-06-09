package user

import "context"

// UserService - UserServiceインターフェース
type UserService interface {
	Authentication(ctx context.Context) (*User, error)
	Create(ctx context.Context, u *User) (*User, error)
	Update(ctx context.Context, u *User) (*User, error)
	UpdatePassword(ctx context.Context, uid string, password string) error
	UploadThumbnail(ctx context.Context, data []byte) (string, error)
	UniqueCheckEmail(ctx context.Context, auth *User, email string) bool
	UniqueCheckUsername(ctx context.Context, auth *User, username string) bool
	UserIDExists(ctx context.Context, userID string) (bool, error)
	GroupIDExists(ctx context.Context, userID string, groupID string) (bool, error)
}
