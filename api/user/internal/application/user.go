package application

import (
	"context"
	"encoding/base64"
	"strings"

	"github.com/calmato/presto-pay/api/user/internal/application/request"
	"github.com/calmato/presto-pay/api/user/internal/application/validation"
	"github.com/calmato/presto-pay/api/user/internal/domain"
	"github.com/calmato/presto-pay/api/user/internal/domain/user"
	"golang.org/x/xerrors"
)

// UserApplication - UserApplicationインターフェース
type UserApplication interface {
	Create(ctx context.Context, req *request.CreateUser) (*user.User, error)
	Update(ctx context.Context, req *request.UpdateUser) (*user.User, error)
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

func (ua *userApplication) Create(ctx context.Context, req *request.CreateUser) (*user.User, error) {
	if ves := ua.userRequestValidation.CreateUser(req); len(ves) > 0 {
		err := xerrors.New("Failed to RequestValidation")
		return nil, domain.InvalidRequestValidation.New(err, ves...)
	}

	thumbnailURL, err := getThumbnailURL(ctx, ua, req.Thumbnail)
	if err != nil {
		err = xerrors.Errorf("Failed to Application: %w", err)
		return nil, domain.UnableConvertStringToByte64.New(err)
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

func (ua *userApplication) Update(ctx context.Context, req *request.UpdateUser) (*user.User, error) {
	u, err := ua.userService.Authentication(ctx)
	if err != nil {
		return nil, domain.Unauthorized.New(err)
	}

	if ves := ua.userRequestValidation.UpdateUser(req); len(ves) > 0 {
		err := xerrors.New("Failed to RequestValidation")
		return nil, domain.InvalidRequestValidation.New(err, ves...)
	}

	thumbnailURL, err := getThumbnailURL(ctx, ua, req.Thumbnail)
	if err != nil {
		err = xerrors.Errorf("Failed to Application: %w", err)
		return nil, domain.UnableConvertStringToByte64.New(err)
	}

	u.Name = req.Name
	u.Username = req.Username
	u.Email = req.Email
	u.Language = req.Language
	u.ThumbnailURL = thumbnailURL

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
			return "", err
		}

		thumbnailURL, err = ua.userService.UploadThumbnail(ctx, data)
		if err != nil {
			return "", err
		}
	}

	return thumbnailURL, nil
}
