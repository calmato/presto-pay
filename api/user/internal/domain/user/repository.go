package user

import "context"

// UserRepository - UserRepositoryインターフェース
type UserRepository interface {
	Authentication(ctx context.Context) (*User, error)
	Create(ctx context.Context, u *User) error
	GetUIDByEmail(ctx context.Context, email string) (string, error)
}
