package user

import "context"

// UserService - UserServiceインターフェース
type UserService interface {
	Authentication(ctx context.Context) (*User, error)
	CreateUnauthorizedUser(ctx context.Context, name string, thumbnail string) (*User, error)
	UpdateUnauthorizedUser(ctx context.Context, userID string, name string, thumbnail string) (*User, error)
	ContainsGroupID(ctx context.Context, u *User, groupID string) (bool, error)
}
