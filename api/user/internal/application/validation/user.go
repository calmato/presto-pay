package validation

import (
	"github.com/calmato/presto-pay/api/user/internal/application/request"
	"github.com/calmato/presto-pay/api/user/internal/domain"
)

// UserRequestValidation - ユーザー関連のバリデーション
type UserRequestValidation interface {
	SearchUsersByUsername(req *request.SearchUsersByUsername) []*domain.ValidationError
	CreateUser(req *request.CreateUser) []*domain.ValidationError
	UpdateProfile(req *request.UpdateProfile) []*domain.ValidationError
	UpdatePassword(req *request.UpdateUserPassword) []*domain.ValidationError
	UniqueCheckEmail(req *request.UniqueCheckUserEmail) []*domain.ValidationError
	UniqueCheckUsername(req *request.UniqueCheckUserUsername) []*domain.ValidationError
}

type userRequestValidation struct {
	validator RequestValidator
}

// NewUserRequestValidation - UserRequestValidationの生成
func NewUserRequestValidation() UserRequestValidation {
	rv := NewRequestValidator()

	return &userRequestValidation{
		validator: rv,
	}
}

func (urv *userRequestValidation) SearchUsersByUsername(req *request.SearchUsersByUsername) []*domain.ValidationError {
	return urv.validator.Run(req)
}

func (urv *userRequestValidation) CreateUser(req *request.CreateUser) []*domain.ValidationError {
	return urv.validator.Run(req)
}

func (urv *userRequestValidation) UpdateProfile(req *request.UpdateProfile) []*domain.ValidationError {
	return urv.validator.Run(req)
}

func (urv *userRequestValidation) UpdatePassword(req *request.UpdateUserPassword) []*domain.ValidationError {
	return urv.validator.Run(req)
}

func (urv *userRequestValidation) UniqueCheckEmail(req *request.UniqueCheckUserEmail) []*domain.ValidationError {
	return urv.validator.Run(req)
}

func (urv *userRequestValidation) UniqueCheckUsername(req *request.UniqueCheckUserUsername) []*domain.ValidationError {
	return urv.validator.Run(req)
}
