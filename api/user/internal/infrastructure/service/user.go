package service

import (
	"context"
	"time"

	"github.com/calmato/presto-pay/api/user/internal/domain/user"
	"github.com/google/uuid"
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

func (us *userService) Create(ctx context.Context, u *user.User) error {
	if ves := us.userDomainValidation.User(ctx, u); len(ves) > 0 {
		err := xerrors.New("Failed to Domain/DomainValidation")

		if isContainCustomUniqueError(ves) {
			return nil, domain.AlreadyExists.New(err, ves...)
		}

		return nil, domain.Unknown.New(err, ves...)
	}

	current := time.Now()

	u.ID = uuid.New().String()
	u.CreatedAt = current
	u.UpdatedAt = current

	if err := us.userRepository.Create(ctx, u); err != nil {
		err = xerrors.Errorf("Failed to Domain/Repository: %w", err)
		return domain.ErrorInDatastore.New(err)
	}

	return nil
}

func (us *userService) UploadThumbnail(ctx context.Context, data []byte) (string, error) {
	thumbnailURL, err := us.userUploader.UploadThumbnail(ctx, data)
	if err != nil {
		err = xerrors.Errorf("Failed to Domain/Uploader: %w", err)
		return "", domain.ErrorInStorage.New(err)
	}

	return thumbnailURL, nil
}

func isContainCustomUniqueError(ves []*domain.ValidationError) bool {
	for _, v := range ves {
		if v.Message == validation.CustomUniqueMessage {
			return true
		}
	}

	return false
}
