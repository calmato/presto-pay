package application

import (
	"context"

	"github.com/calmato/presto-pay/api/calc/internal/application/request"
	"github.com/calmato/presto-pay/api/calc/internal/domain"
	"github.com/calmato/presto-pay/api/calc/internal/domain/group"
	"github.com/calmato/presto-pay/api/calc/internal/domain/user"
)

// GroupApplication - GroupApplicationインターフェース
type GroupApplication interface {
	Create(ctx context.Context, req *request.CreateGroup) (*group.Group, error)
}

type groupApplication struct {
	userService user.UserService
}

// NewGroupApplication - GroupApplicationの生成
func NewGroupApplication(us user.UserService) GroupApplication {
	return &groupApplication{
		userService: us,
	}
}

func (ga *groupApplication) Create(ctx context.Context, req *request.CreateGroup) (*group.Group, error) {
	_, err := ga.userService.Authentication(ctx)
	if err != nil {
		return nil, domain.Unauthorized.New(err)
	}

	// TODO: create group

	return nil, nil
}
