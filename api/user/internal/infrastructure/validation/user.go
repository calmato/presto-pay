package validation

import (
	"context"

	"github.com/calmato/presto-pay/api/user/internal/domain"
	"github.com/calmato/presto-pay/api/user/internal/domain/user"
	"golang.org/x/xerrors"
)

type userDomainValidation struct {
	userRepository user.UserRepository
}

// NewUserDomainValidation - UserDomainValidationの生成
func NewUserDomainValidation(ur user.UserRepository) user.UserDomainValidation {
	return &userDomainValidation{
		userRepository: ur,
	}
}

func (udv *userDomainValidation) User(ctx context.Context, u *user.User) []*domain.ValidationError {
	validationErrors := make([]*domain.ValidationError, 0)

	if err := uniqueCheckEmail(ctx, udv.userRepository, u.ID, u.Email); err != nil {
		validationError := &domain.ValidationError{
			Field:   "email",
			Message: domain.CustomUniqueMessage,
		}

		validationErrors = append(validationErrors, validationError)
	}

	if err := uniqueCheckUsername(ctx, udv.userRepository, u.ID, u.Username); err != nil {
		validationError := &domain.ValidationError{
			Field:   "username",
			Message: domain.CustomUniqueMessage,
		}

		validationErrors = append(validationErrors, validationError)
	}

	return validationErrors
}

func uniqueCheckEmail(ctx context.Context, ur user.UserRepository, id string, email string) error {
	uid, _ := ur.GetUIDByEmail(ctx, email)
	if uid == "" || uid == id {
		return nil
	}

	return xerrors.New("This email is already exists.")
}

func uniqueCheckUsername(ctx context.Context, ur user.UserRepository, id string, username string) error {
	u, err := ur.GetUserByUsername(ctx, username)
	if err != nil {
		return err
	}

	if u.ID != id {
		return nil
	}

	return xerrors.New("This username is already exists.")
}
