package service

import (
	"context"

	"github.com/calmato/presto-pay/api/calc/internal/domain"
	"github.com/calmato/presto-pay/api/calc/internal/domain/user"
	"github.com/calmato/presto-pay/api/calc/internal/infrastructure/api"
	"golang.org/x/xerrors"
)

type userService struct {
	apiClient api.APIClient
}

// NewUserService - UserServiceの生成
func NewUserService(apiClient api.APIClient) user.UserService {
	return &userService{
		apiClient: apiClient,
	}
}

func (us *userService) Authentication(ctx context.Context) (*user.User, error) {
	u, err := us.apiClient.Authentication(ctx)
	if err != nil {
		e := xerrors.New("Authorization Header is not contain.") // TODO: refactor
		if xerrors.Is(err, e) {
			return nil, domain.Unauthorized.New(err)
		}

		err = xerrors.Errorf("Failed to API Client: %w", err)
		return nil, domain.ErrorInOtherAPI.New(err)
	}

	return u, nil
}

func (us *userService) AddGroup(ctx context.Context, userID string, groupID string) error {
	if err := us.apiClient.AddGroup(ctx, userID, groupID); err != nil {
		err = xerrors.Errorf("Failed to API Client: %w", err)
		return domain.ErrorInOtherAPI.New(err)
	}

	return nil
}

func (us *userService) RemoveGroup(ctx context.Context, userID string, groupID string) error {
	if err := us.apiClient.RemoveGroup(ctx, userID, groupID); err != nil {
		err = xerrors.Errorf("Failed to API Client: %w", err)
		return domain.ErrorInOtherAPI.New(err)
	}

	return nil
}
