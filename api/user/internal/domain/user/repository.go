package user

import "context"

type UserRepository interface{
	func CreateUser(ctx context.Context, u *User) error
	func GetUIDByEmail(ctx context.Context, email string) (string, error)
}
