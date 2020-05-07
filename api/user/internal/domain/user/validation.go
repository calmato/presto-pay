package user

import (
	"context"

	"github.com/calmato/presto-pay/api/user/internal/domain"
)

// UserDomainValidation - UserDomainValidationインターフェース
type UserDomainValidation interface {
	User(ctx context.Context, u *User) []*domain.ValidationError
}
