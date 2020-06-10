package user

import "context"

// UserRepository - UserRepositoryインターフェース
type UserRepository interface {
	Authentication(ctx context.Context) (*User, error)
	Create(ctx context.Context, u *User) error
	Update(ctx context.Context, u *User) error
	UpdatePassword(ctx context.Context, uid string, password string) error
	GetUIDByEmail(ctx context.Context, email string) (string, error)
	GetUserByUsername(ctx context.Context, username string) (*User, error)
	GetUserByUserID(ctx context.Context, userID string) (*User, error)
}
