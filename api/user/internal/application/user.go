package application

import (
	"context"
	"encoding/base64"
	"strings"

	"github.com/calmato/presto-pay/api/user/internal/application/request"
	"github.com/calmato/presto-pay/api/user/internal/application/validation"
	"github.com/calmato/presto-pay/api/user/internal/domain/user"
)

// UserApplication - UserApplicationインターフェース
type UserApplication interface {
	Create(ctx context.Context, req *request.CreateUser) error
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

func (ua *userApplication) Create(ctx context.Context, req *request.CreateUser) error {
	if ves := ua.userRequestValidation.CreateUser(req); len(ves) > 0 {
		err := xerrors.New("Failed to Application/RequestValidation")
		return domain.InvalidRequestValidation.New(err, ves...)
	}

	thumbnailURL := ""

	if req.Thumbnail != "" {
		// data:image/png;base64,iVBORw0KGgoAAAA... みたいなのうちの
		// `data:image/png;base64,` の部分を無くした []byte を取得
		b64data := req.Thumbnail[strings.IndexByte(req.Thumbnail, ',')+1:]

		data, err := base64.StdEncoding.DecodeString(b64data)
		if err != nil {
			err = xerrors.Errorf("Failed to Application: %w", err)
			return domain.Unknown.New(err) // TODO: error handling
		}

		thumbnailURL, err = ua.userService.UploadThumbnail(ctx, data)
		if err != nil {
			return err
		}
	}

	u := &domain.User{
		Nmae:         req.Name,
		DisplayName:  req.DisplayName,
		Email:        req.Email,
		thumbnailURL: thumbnailURL,
		Password:     req.Password,
	}

	if err := ua.userService.Create(ctx, u); err != nil {
		return err
	}

	return nil
}
