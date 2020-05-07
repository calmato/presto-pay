package user

import "context"

// UserRepository - UserRepositoryインターフェース
type UserRepository interface {
	Create(ctx context.Context, u *User) error
	GetUIDByEmail(ctx context.Context, email string) (string, error)
}
