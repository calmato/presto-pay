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
	Index(ctx context.Context) ([]*group.Group, error)
	Show(ctx context.Context, groupID string) (*group.Group, error)
	Create(ctx context.Context, req *request.CreateGroup) (*group.Group, error)
	Update(ctx context.Context, req *request.UpdateGroup, groupID string) (*group.Group, error)
	AddUsers(ctx context.Context, req *request.AddUsersInGroup, groupID string) (*group.Group, error)
	RemoveUsers(ctx context.Context, req *request.RemoveUsersInGroup, groupID string) (*group.Group, error)
	Destroy(ctx context.Context, groupID string) error
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

func (ga *groupApplication) Index(ctx context.Context) ([]*group.Group, error) {
	u, err := ga.userService.Authentication(ctx)
	if err != nil {
		return nil, domain.Unauthorized.New(err)
	}

	gs, err := ga.groupService.Index(ctx, u)
	if err != nil {
		return nil, err
	}

	return gs, nil
}

func (ga *groupApplication) Show(ctx context.Context, groupID string) (*group.Group, error) {
	u, err := ga.userService.Authentication(ctx)
	if err != nil {
		return nil, domain.Unauthorized.New(err)
	}

	contain, err := ga.userService.ContainsGroupID(ctx, u, groupID)
	if err != nil {
		return nil, err
	}

	if !contain {
		err := xerrors.New("Failed to Service")
		return nil, domain.Forbidden.New(err)
	}

	g, err := ga.groupService.Show(ctx, groupID)
	if err != nil {
		return nil, err
	}

	return g, nil
}

func (ga *groupApplication) Create(ctx context.Context, req *request.CreateGroup) (*group.Group, error) {
	u, err := ga.userService.Authentication(ctx)
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

	contrains, err := ga.groupService.ContainsUserID(ctx, g, u.ID)
	if err != nil {
		return nil, err
	}

	if !contrains {
		g.UserIDs = append(g.UserIDs, u.ID)
	}

	if _, err = ga.groupService.Create(ctx, g); err != nil {
		return nil, err
	}

	return g, nil
}

func (ga *groupApplication) Update(
	ctx context.Context, req *request.UpdateGroup, groupID string,
) (*group.Group, error) {
	u, err := ga.userService.Authentication(ctx)
	if err != nil {
		return nil, domain.Unauthorized.New(err)
	}

	contain, err := ga.userService.ContainsGroupID(ctx, u, groupID)
	if err != nil {
		return nil, err
	}

	if !contain {
		err := xerrors.New("Failed to Application")
		return nil, domain.Forbidden.New(err)
	}

	if ves := ga.groupRequestValidation.UpdateGroup(req); len(ves) > 0 {
		err := xerrors.New("Failed to RequestValidation")
		return nil, domain.InvalidRequestValidation.New(err, ves...)
	}

	g, err := ga.groupService.Show(ctx, groupID)
	if err != nil {
		err := xerrors.New("Faliled to Application")
		return nil, domain.NotFound.New(err)
	}

	thumbnailURL, err := getThumbnailURL(ctx, ga, req.Thumbnail)
	if err != nil {
		return nil, err
	}

	if thumbnailURL != "" {
		g.ThumbnailURL = thumbnailURL
	}

	g.Name = req.Name

	// TODO: Update Group
	if _, err = ga.groupService.Update(ctx, g, req.UserIDs); err != nil {
		return nil, err
	}

	return g, nil
}

func (ga *groupApplication) AddUsers(
	ctx context.Context, req *request.AddUsersInGroup, groupID string,
) (*group.Group, error) {
	u, err := ga.userService.Authentication(ctx)
	if err != nil {
		return nil, domain.Unauthorized.New(err)
	}

	contain, err := ga.userService.ContainsGroupID(ctx, u, groupID)
	if err != nil {
		return nil, err
	}

	if !contain {
		err := xerrors.New("Failed to Application")
		return nil, domain.Forbidden.New(err)
	}

	if ves := ga.groupRequestValidation.AddUsersInGroup(req); len(ves) > 0 {
		err := xerrors.New("Failed to RequestValidation")
		return nil, domain.InvalidRequestValidation.New(err, ves...)
	}

	g, err := ga.groupService.AddUsers(ctx, groupID, req.UserIDs)
	if err != nil {
		return nil, err
	}

	return g, nil
}

func (ga *groupApplication) RemoveUsers(
	ctx context.Context, req *request.RemoveUsersInGroup, groupID string,
) (*group.Group, error) {
	u, err := ga.userService.Authentication(ctx)
	if err != nil {
		return nil, domain.Unauthorized.New(err)
	}

	contain, err := ga.userService.ContainsGroupID(ctx, u, groupID)
	if err != nil {
		return nil, err
	}

	if !contain {
		err := xerrors.New("Failed to Application")
		return nil, domain.Forbidden.New(err)
	}

	if ves := ga.groupRequestValidation.RemoveUsersInGroup(req); len(ves) > 0 {
		err := xerrors.New("Failed to RequestValidation")
		return nil, domain.InvalidRequestValidation.New(err, ves...)
	}

	g, err := ga.groupService.RemoveUsers(ctx, groupID, req.UserIDs)
	if err != nil {
		return nil, err
	}

	return g, nil
}

func (ga *groupApplication) Destroy(ctx context.Context, groupID string) error {
	u, err := ga.userService.Authentication(ctx)
	if err != nil {
		return domain.Unauthorized.New(err)
	}

	contain, err := ga.userService.ContainsGroupID(ctx, u, groupID)
	if err != nil {
		return err
	}

	if !contain {
		err := xerrors.New("Failed to Application")
		return domain.Forbidden.New(err)
	}

	_, err = ga.groupService.Show(ctx, groupID)
	if err != nil {
		return err
	}

	if err = ga.groupService.Destroy(ctx, groupID); err != nil {
		return err
	}

	return nil
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
