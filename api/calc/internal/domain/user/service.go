package user

import "context"

// UserService - UserServiceインターフェース
type UserService interface {
	Authentication(ctx context.Context) (*User, error)
}
