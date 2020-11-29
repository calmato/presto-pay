package validation

import (
	"github.com/calmato/presto-pay/api/calc/internal/application/request"
	"github.com/calmato/presto-pay/api/calc/internal/domain"
)

// GroupRequestValidation - グループ関連のバリデーション
type GroupRequestValidation interface {
	CreateGroup(req *request.CreateGroup) []*domain.ValidationError
	UpdateGroup(req *request.UpdateGroup) []*domain.ValidationError
	AddUsersInGroup(req *request.AddUsersInGroup) []*domain.ValidationError
	AddUnauthorizedUsersInGroup(req *request.AddUnauthorizedUsersInGroup) []*domain.ValidationError
	RemoveUsersInGroup(req *request.RemoveUsersInGroup) []*domain.ValidationError
	RemoveUnauthorizedUsersInGroup(req *request.RemoveUnauthorizedUsersInGroup) []*domain.ValidationError
}

type groupRequestValidation struct {
	validator RequestValidator
}

// NewGroupRequestValidation - GroupRequestValidationの生成
func NewGroupRequestValidation() GroupRequestValidation {
	rv := NewRequestValidator()

	return &groupRequestValidation{
		validator: rv,
	}
}

func (grv *groupRequestValidation) CreateGroup(req *request.CreateGroup) []*domain.ValidationError {
	return grv.validator.Run(req)
}

func (grv *groupRequestValidation) UpdateGroup(req *request.UpdateGroup) []*domain.ValidationError {
	return grv.validator.Run(req)
}

func (grv *groupRequestValidation) AddUsersInGroup(req *request.AddUsersInGroup) []*domain.ValidationError {
	return grv.validator.Run(req)
}

func (grv *groupRequestValidation) AddUnauthorizedUsersInGroup(
	req *request.AddUnauthorizedUsersInGroup,
) []*domain.ValidationError {
	return grv.validator.Run(req)
}

func (grv *groupRequestValidation) RemoveUsersInGroup(req *request.RemoveUsersInGroup) []*domain.ValidationError {
	return grv.validator.Run(req)
}

func (grv *groupRequestValidation) RemoveUnauthorizedUsersInGroup(
	req *request.RemoveUnauthorizedUsersInGroup,
) []*domain.ValidationError {
	return grv.validator.Run(req)
}
