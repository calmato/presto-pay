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
	ves := make([]*domain.ValidationError, 0)

	if err := uniqueCheckEmail(ctx, udv.userRepository, u.ID, u.Email); err != nil {
		ve := &domain.ValidationError{
			Field:   "email",
			Message: domain.CustomUniqueMessage,
		}

		ves = append(ves, ve)
	}

	if err := uniqueCheckUsername(ctx, udv.userRepository, u.ID, u.Username); err != nil {
		ve := &domain.ValidationError{
			Field:   "username",
			Message: domain.CustomUniqueMessage,
		}

		ves = append(ves, ve)
	}

	if err := uniqueCheckGroupIDs(u.GroupIDs); err != nil {
		ve := &domain.ValidationError{
			Field:   "groupIds",
			Message: domain.CustomUniqueMessage,
		}

		ves = append(ves, ve)
	}

	return ves
}

func uniqueCheckEmail(ctx context.Context, ur user.UserRepository, id string, email string) error {
	// 未登録ユーザの場合、バリデーションチェックはスキップ
	if email == "" {
		return nil
	}

	// 登録済みユーザの場合、バリデーションチェック
	uid, _ := ur.GetUIDByEmail(ctx, email)
	if uid == "" || id == uid {
		return nil
	}

	return xerrors.New("This email is already exists.")
}

func uniqueCheckUsername(ctx context.Context, ur user.UserRepository, id string, username string) error {
	// 未登録ユーザの場合、バリデーションチェックはスキップ
	if username == "" {
		return nil
	}

	// 登録済みユーザの場合、バリデーションチェック
	u, _ := ur.ShowByUsername(ctx, username)
	if u == nil || u.ID == "" {
		return nil
	}

	if id == "" || id != u.ID {
		return xerrors.New("This username is already exists.")
	}

	return nil
}

func uniqueCheckGroupIDs(groupIDs []string) error {
	m := make(map[string]struct{})

	for _, v := range groupIDs {
		if _, ok := m[v]; ok {
			return xerrors.New("There are duplicate values.")
		}

		m[v] = struct{}{}
	}

	return nil
}
