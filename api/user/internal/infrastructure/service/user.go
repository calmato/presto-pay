package service

import (
	"context"
	"time"

	"github.com/calmato/presto-pay/api/user/internal/domain"
	"github.com/calmato/presto-pay/api/user/internal/domain/user"
	"github.com/google/uuid"
	"golang.org/x/xerrors"
)

type userService struct {
	userDomainValidation user.UserDomainValidation
	userRepository       user.UserRepository
	userUploader         user.UserUploader
}

// NewUserService - UserServiceの生成
func NewUserService(
	udv user.UserDomainValidation, ur user.UserRepository, uu user.UserUploader,
) user.UserService {
	return &userService{
		userDomainValidation: udv,
		userRepository:       ur,
		userUploader:         uu,
	}
}

func (us *userService) Authentication(ctx context.Context) (*user.User, error) {
	u, err := us.userRepository.Authentication(ctx)
	if err != nil {
		err = xerrors.Errorf("Failed to Repositoty: %w", err)
		return nil, domain.Unauthorized.New(err)
	}

	return u, nil
}

func (us *userService) Create(ctx context.Context, u *user.User) (*user.User, error) {
	if ves := us.userDomainValidation.User(ctx, u); len(ves) > 0 {
		err := xerrors.New("Failed to DomainValidation")

		if isContainCustomUniqueError(ves) {
			return nil, domain.AlreadyExistsInDatastore.New(err, ves...)
		}

		return nil, domain.Unknown.New(err, ves...)
	}

	current := time.Now()

	u.ID = uuid.New().String()
	u.CreatedAt = current
	u.UpdatedAt = current

	if err := us.userRepository.Create(ctx, u); err != nil {
		err = xerrors.Errorf("Failed to Repository: %w", err)
		return nil, domain.ErrorInDatastore.New(err)
	}

	return u, nil
}

func (us *userService) Update(ctx context.Context, u *user.User) (*user.User, error) {
	if ves := us.userDomainValidation.User(ctx, u); len(ves) > 0 {
		err := xerrors.New("Failed to DomainValidation")

		if isContainCustomUniqueError(ves) {
			return nil, domain.AlreadyExistsInDatastore.New(err, ves...)
		}

		return nil, domain.Unknown.New(err, ves...)
	}

	current := time.Now()

	u.UpdatedAt = current

	if err := us.userRepository.Update(ctx, u); err != nil {
		err = xerrors.Errorf("Failed to Repository: %w", err)
		return nil, domain.ErrorInDatastore.New(err)
	}

	return u, nil
}

func (us *userService) UpdatePassword(ctx context.Context, uid string, password string) error {
	if err := us.userRepository.UpdatePassword(ctx, uid, password); err != nil {
		return domain.ErrorInDatastore.New(err)
	}

	return nil
}

func (us *userService) UploadThumbnail(ctx context.Context, data []byte) (string, error) {
	thumbnailURL, err := us.userUploader.UploadThumbnail(ctx, data)
	if err != nil {
		err = xerrors.Errorf("Failed to Uploader: %w", err)
		return "", domain.ErrorInStorage.New(err)
	}

	return thumbnailURL, nil
}

func (us *userService) UniqueCheckEmail(ctx context.Context, au *user.User, email string) bool {
	uid, _ := us.userRepository.GetUIDByEmail(ctx, email)
	if uid == "" {
		return true
	}

	if au == nil || au.ID != uid {
		return false
	}

	return true
}

func (us *userService) UniqueCheckUsername(ctx context.Context, au *user.User, username string) bool {
	u, _ := us.userRepository.GetUserByUsername(ctx, username)
	if u == nil || u.ID == "" {
		return true
	}

	if au == nil || au.ID != u.ID {
		return false
	}

	return true
}

func isContainCustomUniqueError(ves []*domain.ValidationError) bool {
	for _, v := range ves {
		if v.Message == domain.CustomUniqueMessage {
			return true
		}
	}

	return false
}
