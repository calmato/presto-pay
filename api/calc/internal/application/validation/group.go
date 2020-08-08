package validation

import (
	"github.com/calmato/presto-pay/api/calc/internal/application/request"
	"github.com/calmato/presto-pay/api/calc/internal/domain"
)

// GroupRequestValidation - グループ関連のバリデーション
type GroupRequestValidation interface {
	CreateGroup(req *request.CreateGroup) []*domain.ValidationError
	AddUsersInGroup(req *request.AddUsersInGroup) []*domain.ValidationError
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

func (grv *groupRequestValidation) AddUsersInGroup(req *request.AddUsersInGroup) []*domain.ValidationError {
	return grv.validator.Run(req)
}
