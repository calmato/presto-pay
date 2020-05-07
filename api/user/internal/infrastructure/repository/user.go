package repository

import (
	"context"

	"github.com/calmato/presto-pay/api/user/internal/domain/user"
	"github.com/calmato/presto-pay/api/user/lib/firebase/authentication"
	"github.com/calmato/presto-pay/api/user/lib/firebase/firestore"
)

type userRepository struct {
	auth      *authentication.Auth
	firestore *firestore.Firestore
}

// NewUserRepository - UserRepositoryの生成
func NewUserRepository(fa *authentication.Auth, fs *firestore.Firestore) user.UserRepository {
	return &userRepository{
		auth:      fa,
		firestore: fs,
	}
}

func (ur *userRepository) Create(ctx context.Context, u *user.User) error {
	userReference := getUserReference()

	_, err := ur.auth.CreateUser(ctx, u.ID, u.Email, u.Password)
	if err != nil {
		return err
	}

	if err = ur.firestore.Set(ctx, userReference, u.ID, u); err != nil {
		return err
	}

	return nil
}

func (ur *userRepository) GetUIDByEmail(ctx context.Context, email string) (string, error) {
	uid, err := ur.auth.GetUIDByEmail(ctx, email)
	if err != nil {
		return "", err
	}

	return uid, nil
}
