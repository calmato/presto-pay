package user

import "context"

// UserService - UserServiceインターフェース
type UserService interface {
	Authentication(ctx context.Context) (*User, error)
	ContainsGroupID(ctx context.Context, u *User, groupID string) (bool, error)
}
