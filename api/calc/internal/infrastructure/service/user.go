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

func (us *userService) CreateUnauthorizedUser(
	ctx context.Context, name string, thumbnail string,
) (*user.User, error) {
	u, err := us.apiClient.CreateUnauthorizedUser(ctx, name, thumbnail)
	if err != nil {
		return nil, err
	}

	return u, nil
}

func (us *userService) ContainsGroupID(ctx context.Context, u *user.User, groupID string) (bool, error) {
	if u == nil {
		err := xerrors.New("User is empty")
		return false, domain.NotFound.New(err)
	}

	for _, v := range u.GroupIDs {
		if v == groupID {
			return true, nil
		}
	}

	return false, nil
}
