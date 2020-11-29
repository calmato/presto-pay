package service

import (
	"context"
	"strings"
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

	if u.GroupIDs == nil {
		u.GroupIDs = make([]string, 0)
	}

	if u.HiddenGroupIDs == nil {
		u.HiddenGroupIDs = make([]string, 0)
	}

	if u.FriendIDs == nil {
		u.FriendIDs = make([]string, 0)
	}

	return u, nil
}

func (us *userService) IndexByUsername(ctx context.Context, username string, startAt string) ([]*user.User, error) {
	if startAt == "" {
		u, err := us.userRepository.IndexByUsername(ctx, username)
		if err != nil {
			return nil, err
		}

		return u, nil
	}

	u, err := us.userRepository.IndexByUsernameFromStartAt(ctx, username, startAt)
	if err != nil {
		return nil, err
	}

	return u, nil
}

func (us *userService) IndexFriends(ctx context.Context, u *user.User) ([]*user.User, error) {
	if u == nil {
		err := xerrors.New("User is empty")
		return nil, domain.NotFound.New(err)
	}

	users := make([]*user.User, len(u.FriendIDs))
	for i, friendID := range u.FriendIDs {
		user, err := us.userRepository.ShowByUserID(ctx, friendID)
		if err != nil {
			err = xerrors.Errorf("Failed to Repository: %w", err)
			return nil, domain.NotFound.New(err)
		}

		users[i] = user
	}

	return users, nil
}

func (us *userService) Show(ctx context.Context, userID string) (*user.User, error) {
	u, err := us.userRepository.ShowByUserID(ctx, userID)
	if err != nil {
		err = xerrors.Errorf("Failed to Repository: %w", err)
		return nil, domain.NotFound.New(err)
	}

	if u.GroupIDs == nil {
		u.GroupIDs = make([]string, 0)
	}

	if u.HiddenGroupIDs == nil {
		u.HiddenGroupIDs = make([]string, 0)
	}

	if u.FriendIDs == nil {
		u.FriendIDs = make([]string, 0)
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
	u.UsernameLower = strings.ToLower(u.Username)
	u.Email = strings.ToLower(u.Email)
	u.GroupIDs = make([]string, 0)
	u.FriendIDs = make([]string, 0)
	u.CreatedAt = current
	u.UpdatedAt = current

	if err := us.userRepository.Create(ctx, u); err != nil {
		err = xerrors.Errorf("Failed to Repository: %w", err)
		return nil, domain.ErrorInDatastore.New(err)
	}

	return u, nil
}

func (us *userService) CreateUnauthorizedUser(ctx context.Context, u *user.User) (*user.User, error) {
	if ves := us.userDomainValidation.User(ctx, u); len(ves) > 0 {
		err := xerrors.New("Failed to DomainValidation")

		if isContainCustomUniqueError(ves) {
			return nil, domain.AlreadyExistsInDatastore.New(err, ves...)
		}

		return nil, domain.Unknown.New(err, ves...)
	}

	current := time.Now()

	u.ID = uuid.New().String()
	u.GroupIDs = make([]string, 0)
	u.FriendIDs = make([]string, 0)
	u.CreatedAt = current
	u.UpdatedAt = current

	if err := us.userRepository.CreateUnauthorizedUser(ctx, u); err != nil {
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

	u.UsernameLower = strings.ToLower(u.Username)
	u.Email = strings.ToLower(u.Email)
	u.UpdatedAt = current

	if err := us.userRepository.Update(ctx, u); err != nil {
		err = xerrors.Errorf("Failed to Repository: %w", err)
		return nil, domain.ErrorInDatastore.New(err)
	}

	return u, nil
}

func (us *userService) UpdateUnauthorizedUser(ctx context.Context, u *user.User) (*user.User, error) {
	if ves := us.userDomainValidation.User(ctx, u); len(ves) > 0 {
		err := xerrors.New("Failed to DomainValidation")

		if isContainCustomUniqueError(ves) {
			return nil, domain.AlreadyExistsInDatastore.New(err, ves...)
		}

		return nil, domain.Unknown.New(err, ves...)
	}

	current := time.Now()

	u.UpdatedAt = current

	if err := us.userRepository.UpdateUnauthorizedUser(ctx, u); err != nil {
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

func (us *userService) UniqueCheckEmail(ctx context.Context, u *user.User, email string) bool {
	uid, _ := us.userRepository.GetUIDByEmail(ctx, email)
	if uid == "" {
		return true
	}

	if u == nil || u.ID != uid {
		return false
	}

	return true
}

func (us *userService) UniqueCheckUsername(ctx context.Context, u *user.User, username string) bool {
	res, _ := us.userRepository.ShowByUsername(ctx, username)
	if res == nil || res.ID == "" {
		return true
	}

	if u == nil || u.ID != res.ID {
		return false
	}

	return true
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

func (us *userService) ContainsHiddenGroupID(ctx context.Context, u *user.User, groupID string) (bool, error) {
	if u == nil {
		err := xerrors.New("User is empty")
		return false, domain.NotFound.New(err)
	}

	for _, v := range u.HiddenGroupIDs {
		if v == groupID {
			return true, nil
		}
	}

	return false, nil
}

func (us *userService) ContainsFriendID(ctx context.Context, u *user.User, friendID string) (bool, error) {
	if u == nil {
		err := xerrors.New("User is empty")
		return false, domain.NotFound.New(err)
	}

	for _, v := range u.FriendIDs {
		if v == friendID {
			return true, nil
		}
	}

	return false, nil
}

func isContainCustomUniqueError(ves []*domain.ValidationError) bool {
	for _, v := range ves {
		if v.Message == domain.CustomUniqueMessage {
			return true
		}
	}

	return false
}
