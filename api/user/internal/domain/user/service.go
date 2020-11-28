package user

import "context"

// UserService - UserServiceインターフェース
type UserService interface {
	Authentication(ctx context.Context) (*User, error)
	IndexByUsername(ctx context.Context, username string, startAt string) ([]*User, error)
	IndexFriends(ctx context.Context, u *User) ([]*User, error)
	Show(ctx context.Context, userID string) (*User, error)
	Create(ctx context.Context, u *User) (*User, error)
	CreateUnauthorizedUser(ctx context.Context, u *User) (*User, error)
	Update(ctx context.Context, u *User) (*User, error)
	UpdatePassword(ctx context.Context, uid string, password string) error
	UpdateUnauthorizedUser(ctx context.Context, u *User) (*User, error)
	UploadThumbnail(ctx context.Context, data []byte) (string, error)
	UniqueCheckEmail(ctx context.Context, u *User, email string) bool
	UniqueCheckUsername(ctx context.Context, u *User, username string) bool
	ContainsGroupID(ctx context.Context, u *User, groupID string) (bool, error)
	ContainsHiddenGroupID(ctx context.Context, u *User, groupID string) (bool, error)
	ContainsFriendID(ctx context.Context, u *User, friendID string) (bool, error)
}
