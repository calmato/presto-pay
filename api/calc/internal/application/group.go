package application

import (
	"context"
	"encoding/base64"
	"strings"

	"github.com/calmato/presto-pay/api/calc/internal/application/request"
	"github.com/calmato/presto-pay/api/calc/internal/application/validation"
	"github.com/calmato/presto-pay/api/calc/internal/domain"
	"github.com/calmato/presto-pay/api/calc/internal/domain/group"
	"github.com/calmato/presto-pay/api/calc/internal/domain/user"
	"golang.org/x/xerrors"
)

// GroupApplication - GroupApplicationインターフェース
type GroupApplication interface {
	Create(ctx context.Context, req *request.CreateGroup) (*group.Group, error)
}

type groupApplication struct {
	groupRequestValidation validation.GroupRequestValidation
	userService            user.UserService
	groupService           group.GroupService
}

// NewGroupApplication - GroupApplicationの生成
func NewGroupApplication(
	grv validation.GroupRequestValidation, us user.UserService, gs group.GroupService,
) GroupApplication {
	return &groupApplication{
		groupRequestValidation: grv,
		userService:            us,
		groupService:           gs,
	}
}

func (ga *groupApplication) Create(ctx context.Context, req *request.CreateGroup) (*group.Group, error) {
	_, err := ga.userService.Authentication(ctx)
	if err != nil {
		return nil, domain.Unauthorized.New(err)
	}

	if ves := ga.groupRequestValidation.CreateGroup(req); len(ves) > 0 {
		err := xerrors.New("Failed to RequestValidation")
		return nil, domain.InvalidRequestValidation.New(err, ves...)
	}

	thumbnailURL, err := getThumbnailURL(ctx, ga, req.Thumbnail)
	if err != nil {
		return nil, err
	}

	g := &group.Group{
		Name:         req.Name,
		ThumbnailURL: thumbnailURL,
		UserIDs:      req.UserIDs,
	}

	// TODO: g.UserIDsとu.IDの比較してなければ足すってのしたい

	if _, err = ga.groupService.Create(ctx, g); err != nil {
		return nil, err
	}

	return g, nil
}

func getThumbnailURL(ctx context.Context, ga *groupApplication, thumbnail string) (string, error) {
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

		thumbnailURL, err = ga.groupService.UploadThumbnail(ctx, data)
		if err != nil {
			err = xerrors.Errorf("Failed to Application: %w", err)
			return "", domain.ErrorInStorage.New(err)
		}
	}

	return thumbnailURL, nil
}
