package application

import (
	"context"
	"encoding/base64"
	"strings"

	"github.com/calmato/presto-pay/api/user/internal/application/request"
	"github.com/calmato/presto-pay/api/user/internal/application/validation"
	"github.com/calmato/presto-pay/api/user/internal/domain"
	"github.com/calmato/presto-pay/api/user/internal/domain/user"
	"github.com/calmato/presto-pay/api/user/lib/common"
	"golang.org/x/xerrors"
)

// UserApplication - UserApplicationインターフェース
type UserApplication interface {
	IndexByUsername(ctx context.Context, req *request.IndexByUsername) ([]*user.User, error)
	IndexFriends(ctx context.Context) ([]*user.User, error)
	Show(ctx context.Context, userID string) (*user.User, error)
	ShowProfile(ctx context.Context) (*user.User, error)
	Create(ctx context.Context, req *request.CreateUser) (*user.User, error)
	CreateUnauthorizedUser(ctx context.Context, req *request.CreateUnauthorizedUser) (*user.User, error)
	RegisterInstanceID(ctx context.Context, req *request.RegisterInstanceID) (*user.User, error)
	UpdateProfile(ctx context.Context, req *request.UpdateProfile) (*user.User, error)
	UpdateUnauthorizedUser(ctx context.Context, userID string, req *request.UpdateUnauthorizedUser) (*user.User, error)
	UpdatePassword(ctx context.Context, req *request.UpdateUserPassword) (*user.User, error)
	UniqueCheckEmail(ctx context.Context, req *request.UniqueCheckUserEmail) (bool, error)
	UniqueCheckUsername(ctx context.Context, req *request.UniqueCheckUserUsername) (bool, error)
	AddGroup(ctx context.Context, userID string, groupID string) (*user.User, error)
	RemoveGroup(ctx context.Context, userID string, groupID string) (*user.User, error)
	AddHiddenGroup(ctx context.Context, groupID string) (*user.User, error)
	RemoveHiddenGroup(ctx context.Context, groupID string) (*user.User, error)
	AddFriend(ctx context.Context, req *request.AddFriend) (*user.User, error)
	RemoveFriend(ctx context.Context, userID string) (*user.User, error)
}

type userApplication struct {
	userRequestValidation validation.UserRequestValidation
	userService           user.UserService
}

// NewUserApplication - UserApplicationの生成
func NewUserApplication(urv validation.UserRequestValidation, us user.UserService) UserApplication {
	return &userApplication{
		userRequestValidation: urv,
		userService:           us,
	}
}

func (ua *userApplication) IndexByUsername(ctx context.Context, req *request.IndexByUsername) ([]*user.User, error) {
	if _, err := ua.userService.Authentication(ctx); err != nil {
		return nil, domain.Unauthorized.New(err)
	}

	if ves := ua.userRequestValidation.IndexByUsername(req); len(ves) > 0 {
		err := xerrors.New("Failed to RequestValidation")
		return nil, domain.InvalidRequestValidation.New(err, ves...)
	}

	us, err := ua.userService.IndexByUsername(ctx, req.Username, req.StartAt)
	if err != nil {
		return nil, err
	}

	return us, nil
}

func (ua *userApplication) IndexFriends(ctx context.Context) ([]*user.User, error) {
	u, err := ua.userService.Authentication(ctx)
	if err != nil {
		return nil, domain.Unauthorized.New(err)
	}

	us, err := ua.userService.IndexFriends(ctx, u)
	if err != nil {
		return nil, err
	}

	return us, nil
}

func (ua *userApplication) Show(ctx context.Context, userID string) (*user.User, error) {
	if _, err := ua.userService.Authentication(ctx); err != nil {
		return nil, domain.Unauthorized.New(err)
	}

	u, err := ua.userService.Show(ctx, userID)
	if err != nil {
		return nil, err
	}

	return u, nil
}

func (ua *userApplication) ShowProfile(ctx context.Context) (*user.User, error) {
	u, err := ua.userService.Authentication(ctx)
	if err != nil {
		return nil, domain.Unauthorized.New(err)
	}

	return u, nil
}

func (ua *userApplication) Create(ctx context.Context, req *request.CreateUser) (*user.User, error) {
	if ves := ua.userRequestValidation.CreateUser(req); len(ves) > 0 {
		err := xerrors.New("Failed to RequestValidation")
		return nil, domain.InvalidRequestValidation.New(err, ves...)
	}

	thumbnailURL, err := getThumbnailURL(ctx, ua, req.Thumbnail)
	if err != nil {
		return nil, err
	}

	u := &user.User{
		Name:         req.Name,
		Username:     req.Username,
		Email:        req.Email,
		ThumbnailURL: thumbnailURL,
		Password:     req.Password,
	}

	if _, err := ua.userService.Create(ctx, u); err != nil {
		return nil, err
	}

	return u, nil
}

func (ua *userApplication) CreateUnauthorizedUser(
	ctx context.Context, req *request.CreateUnauthorizedUser,
) (*user.User, error) {
	if _, err := ua.userService.Authentication(ctx); err != nil {
		return nil, domain.Unauthorized.New(err)
	}

	if ves := ua.userRequestValidation.CreateUnauthorizedUser(req); len(ves) > 0 {
		err := xerrors.New("Failed to RequestValidation")
		return nil, domain.InvalidRequestValidation.New(err, ves...)
	}

	thumbnailURL, err := getThumbnailURL(ctx, ua, req.Thumbnail)
	if err != nil {
		return nil, err
	}

	u := &user.User{
		Name:         req.Name,
		ThumbnailURL: thumbnailURL,
	}

	if _, err := ua.userService.CreateUnauthorizedUser(ctx, u); err != nil {
		return nil, err
	}

	return u, nil
}

func (ua *userApplication) RegisterInstanceID(
	ctx context.Context, req *request.RegisterInstanceID,
) (*user.User, error) {
	u, err := ua.userService.Authentication(ctx)
	if err != nil {
		return nil, domain.Unauthorized.New(err)
	}

	if ves := ua.userRequestValidation.RegisterInstanceID(req); len(ves) > 0 {
		err := xerrors.New("Failed to RequestValidation")
		return nil, domain.InvalidRequestValidation.New(err, ves...)
	}

	if u.InstanceID == req.InstanceID {
		return u, nil
	}

	u.InstanceID = req.InstanceID

	if _, err := ua.userService.Update(ctx, u); err != nil {
		return nil, err
	}

	return u, nil
}

func (ua *userApplication) UpdateProfile(ctx context.Context, req *request.UpdateProfile) (*user.User, error) {
	u, err := ua.userService.Authentication(ctx)
	if err != nil {
		return nil, domain.Unauthorized.New(err)
	}

	if ves := ua.userRequestValidation.UpdateProfile(req); len(ves) > 0 {
		err := xerrors.New("Failed to RequestValidation")
		return nil, domain.InvalidRequestValidation.New(err, ves...)
	}

	thumbnailURL, err := getThumbnailURL(ctx, ua, req.Thumbnail)
	if err != nil {
		return nil, err
	}

	u.Name = req.Name
	u.Username = req.Username
	u.Email = req.Email
	u.ThumbnailURL = thumbnailURL

	if _, err := ua.userService.Update(ctx, u); err != nil {
		return nil, err
	}

	return u, nil
}

func (ua *userApplication) UpdateUnauthorizedUser(
	ctx context.Context, userID string, req *request.UpdateUnauthorizedUser,
) (*user.User, error) {
	au, err := ua.userService.Authentication(ctx)
	if err != nil {
		return nil, domain.Unauthorized.New(err)
	}

	if ves := ua.userRequestValidation.UpdateUnauthorizedUser(req); len(ves) > 0 {
		err := xerrors.New("Failed to RequestValidation")
		return nil, domain.InvalidRequestValidation.New(err, ves...)
	}

	u, err := ua.userService.Show(ctx, userID)
	if err != nil {
		return nil, err
	}

	// 未認証ユーザ -> usernameがnil && nameとthumbnailのみ
	if u.Username != "" {
		err := xerrors.New("Faled to Application")
		return nil, domain.Forbidden.New(err)
	}

	// ログインユーザが所属するユーザーのみ変更できるように...
	if contains := containsGroupID(au.GroupIDs, u.GroupIDs); !contains {
		err := xerrors.New("Faled to Application")
		return nil, domain.Forbidden.New(err)
	}

	thumbnailURL, err := getThumbnailURL(ctx, ua, req.Thumbnail)
	if err != nil {
		return nil, err
	}

	u.Name = req.Name
	u.ThumbnailURL = thumbnailURL

	if _, err := ua.userService.UpdateUnauthorizedUser(ctx, u); err != nil {
		return nil, err
	}

	return u, nil
}

func (ua *userApplication) UpdatePassword(ctx context.Context, req *request.UpdateUserPassword) (*user.User, error) {
	u, err := ua.userService.Authentication(ctx)
	if err != nil {
		return nil, domain.Unauthorized.New(err)
	}

	if ves := ua.userRequestValidation.UpdatePassword(req); len(ves) > 0 {
		err := xerrors.New("Failed to RequestValidation")
		return nil, domain.InvalidRequestValidation.New(err, ves...)
	}

	if err := ua.userService.UpdatePassword(ctx, u.ID, req.Password); err != nil {
		return nil, err
	}

	return u, nil
}

func (ua *userApplication) UniqueCheckEmail(ctx context.Context, req *request.UniqueCheckUserEmail) (bool, error) {
	if ves := ua.userRequestValidation.UniqueCheckEmail(req); len(ves) > 0 {
		err := xerrors.New("Failed to RequestValidation")
		return false, domain.InvalidRequestValidation.New(err, ves...)
	}

	u, _ := ua.userService.Authentication(ctx)
	return ua.userService.UniqueCheckEmail(ctx, u, req.Email), nil
}

func (ua *userApplication) UniqueCheckUsername(
	ctx context.Context, req *request.UniqueCheckUserUsername,
) (bool, error) {
	if ves := ua.userRequestValidation.UniqueCheckUsername(req); len(ves) > 0 {
		err := xerrors.New("Failed to RequestValidation")
		return false, domain.InvalidRequestValidation.New(err, ves...)
	}

	u, _ := ua.userService.Authentication(ctx)
	return ua.userService.UniqueCheckUsername(ctx, u, req.Username), nil
}

func (ua *userApplication) AddGroup(ctx context.Context, userID string, groupID string) (*user.User, error) {
	if _, err := ua.userService.Authentication(ctx); err != nil {
		return nil, domain.Unauthorized.New(err)
	}

	u, err := ua.userService.Show(ctx, userID)
	if err != nil {
		return nil, err
	}

	contains, err := ua.userService.ContainsGroupID(ctx, u, groupID)
	if err != nil {
		return nil, err
	}

	if contains {
		err := xerrors.New("Failed to Service")
		return nil, domain.AlreadyExistsInDatastore.New(err)
	}

	u.GroupIDs = append(u.GroupIDs, groupID)

	// username == "" -> 未認証ユーザ
	if u.Username == "" {
		if _, err := ua.userService.UpdateUnauthorizedUser(ctx, u); err != nil {
			return nil, err
		}
	} else {
		if _, err := ua.userService.Update(ctx, u); err != nil {
			return nil, err
		}
	}

	return u, nil
}

func (ua *userApplication) RemoveGroup(ctx context.Context, userID string, groupID string) (*user.User, error) {
	if _, err := ua.userService.Authentication(ctx); err != nil {
		return nil, domain.Unauthorized.New(err)
	}

	u, err := ua.userService.Show(ctx, userID)
	if err != nil {
		return nil, err
	}

	contains, err := ua.userService.ContainsGroupID(ctx, u, groupID)
	if err != nil {
		return nil, err
	}

	if !contains {
		err := xerrors.New("Failed to Service")
		return u, domain.NotFound.New(err)
	}

	u.GroupIDs = common.RemoveString(u.GroupIDs, groupID)

	if _, err := ua.userService.Update(ctx, u); err != nil {
		return nil, err
	}

	return u, nil
}

func (ua *userApplication) AddHiddenGroup(ctx context.Context, groupID string) (*user.User, error) {
	u, err := ua.userService.Authentication(ctx)
	if err != nil {
		return nil, domain.Unauthorized.New(err)
	}

	contains, err := ua.userService.ContainsHiddenGroupID(ctx, u, groupID)
	if err != nil {
		return nil, err
	}

	if contains {
		err := xerrors.New("Failed to Service")
		return nil, domain.AlreadyExistsInDatastore.New(err)
	}

	u.HiddenGroupIDs = append(u.HiddenGroupIDs, groupID)

	if _, err := ua.userService.Update(ctx, u); err != nil {
		return nil, err
	}

	return u, nil
}

func (ua *userApplication) RemoveHiddenGroup(ctx context.Context, groupID string) (*user.User, error) {
	u, err := ua.userService.Authentication(ctx)
	if err != nil {
		return nil, domain.Unauthorized.New(err)
	}

	contains, err := ua.userService.ContainsHiddenGroupID(ctx, u, groupID)
	if err != nil {
		return nil, err
	}

	if !contains {
		err := xerrors.New("Failed to Service")
		return nil, domain.NotFound.New(err)
	}

	u.HiddenGroupIDs = common.RemoveString(u.HiddenGroupIDs, groupID)

	if _, err := ua.userService.Update(ctx, u); err != nil {
		return nil, err
	}

	return u, nil
}

func (ua *userApplication) AddFriend(ctx context.Context, req *request.AddFriend) (*user.User, error) {
	au, err := ua.userService.Authentication(ctx)
	if err != nil {
		return nil, domain.Unauthorized.New(err)
	}

	if ves := ua.userRequestValidation.AddFriend(req); len(ves) > 0 {
		err := xerrors.New("Failed to RequestValidation")
		return nil, domain.InvalidRequestValidation.New(err, ves...)
	}

	u, err := ua.userService.Show(ctx, req.UserID)
	if err != nil {
		return nil, err
	}

	contains, err := ua.userService.ContainsFriendID(ctx, au, u.ID)
	if err != nil {
		return nil, err
	}

	if contains {
		err := xerrors.New("Failed to Service")
		return nil, domain.AlreadyExistsInDatastore.New(err)
	}

	au.FriendIDs = append(au.FriendIDs, u.ID)

	if _, err := ua.userService.Update(ctx, au); err != nil {
		return nil, err
	}

	return au, nil
}

func (ua *userApplication) RemoveFriend(ctx context.Context, userID string) (*user.User, error) {
	u, err := ua.userService.Authentication(ctx)
	if err != nil {
		return nil, domain.Unauthorized.New(err)
	}

	contains, err := ua.userService.ContainsFriendID(ctx, u, userID)
	if err != nil {
		return nil, err
	}

	if !contains {
		err := xerrors.New("Failed to Service")
		return nil, domain.NotFound.New(err)
	}

	u.FriendIDs = common.RemoveString(u.FriendIDs, userID)

	if _, err := ua.userService.Update(ctx, u); err != nil {
		return nil, err
	}

	return u, nil
}

func getThumbnailURL(ctx context.Context, ua *userApplication, thumbnail string) (string, error) {
	thumbnailURL := ""

	if thumbnail != "" {
		// data:image/png;base64,iVBORw0KGgoAAAA... みたいなのうちの
		// `data:image/png;base64,` の部分を無くした []byte を取得
		b64data := thumbnail[strings.IndexByte(thumbnail, ',')+1:]

		data, err := base64.StdEncoding.DecodeString(b64data)
		if err != nil {
			ve := &domain.ValidationError{
				Field:   "thumbnail",
				Message: domain.UnableConvertBase64Massage,
			}

			return "", domain.UnableConvertBase64.New(err, ve)
		}

		thumbnailURL, err = ua.userService.UploadThumbnail(ctx, data)
		if err != nil {
			err = xerrors.Errorf("Failed to Application: %w", err)
			return "", domain.ErrorInStorage.New(err)
		}
	}

	return thumbnailURL, nil
}

func containsGroupID(authUserGroupIDs []string, unauthorizedUserGroupIDs []string) bool {
	for _, i := range authUserGroupIDs {
		for _, j := range unauthorizedUserGroupIDs {
			if i == j {
				return true
			}
		}
	}

	return false
}
