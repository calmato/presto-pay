package validation

import (
	"github.com/calmato/presto-pay/api/user/internal/application/request"
	"github.com/calmato/presto-pay/api/user/internal/domain"
)

// UserRequestValidation - ユーザー関連のバリデーション
type UserRequestValidation interface {
	CreateUser(req *request.CreateUser) []*domain.ValidationError
	UpdateUser(req *request.UpdateUser) []*domain.ValidationError
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

func (urv *userRequestValidation) CreateUser(req *request.CreateUser) []*domain.ValidationError {
	return urv.validator.Run(req)
}

func (urv *userRequestValidation) UpdateUser(req *request.UpdateUser) []*domain.ValidationError {
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
