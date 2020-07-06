package service

import (
	"context"
	"time"

	"github.com/calmato/presto-pay/api/calc/internal/domain"
	"github.com/calmato/presto-pay/api/calc/internal/domain/group"
	"github.com/calmato/presto-pay/api/calc/internal/domain/user"
	"github.com/calmato/presto-pay/api/calc/internal/infrastructure/api"
	"github.com/google/uuid"
	"golang.org/x/xerrors"
)

type groupService struct {
	groupDomainValidation group.GroupDomainValidation
	groupRepository       group.GroupRepository
	groupUploader         group.GroupUploader
	apiClient             api.APIClient
}

// NewGroupService - GroupServiceの生成
func NewGroupService(
	gdv group.GroupDomainValidation, gr group.GroupRepository, gu group.GroupUploader,
	ac api.APIClient,
) group.GroupService {
	return &groupService{
		groupDomainValidation: gdv,
		groupRepository:       gr,
		groupUploader:         gu,
		apiClient:             ac,
	}
}

func (gs *groupService) IndexJoinGroups(ctx context.Context, u *user.User) ([]*group.Group, error) {
	groups := make([]*group.Group, len(u.GroupIDs))

	for i, groupID := range u.GroupIDs {
		g, err := gs.groupRepository.Show(ctx, groupID)
		if err != nil {
			return nil, domain.ErrorInDatastore.New(err)
		}

		groups[i] = g
	}

	return groups, nil
}

func (gs *groupService) Show(ctx context.Context, groupID string) (*group.Group, error) {
	g, err := gs.groupRepository.Show(ctx, groupID)
	if err != nil {
		return nil, domain.ErrorInDatastore.New(err)
	}

	us := map[string]*user.User{}

	for _, userID := range g.UserIDs {
		u, err := gs.apiClient.ShowUser(ctx, userID)
		if err != nil {
			return nil, domain.ErrorInDatastore.New(err)
		}

		us[userID] = u
	}

	return g, nil
}

func (gs *groupService) Create(ctx context.Context, g *group.Group) (*group.Group, error) {
	if ves := gs.groupDomainValidation.Group(ctx, g); len(ves) > 0 {
		err := xerrors.New("Failed to DomainValidation")
		return nil, domain.Unknown.New(err, ves...)
	}

	current := time.Now()

	g.ID = uuid.New().String()
	g.CreatedAt = current
	g.UpdatedAt = current

	if err := gs.groupRepository.Create(ctx, g); err != nil {
		err = xerrors.Errorf("Failed to Repository: %w", err)
		return nil, domain.ErrorInDatastore.New(err)
	}

	for _, userID := range g.UserIDs {
		if err := gs.apiClient.AddGroup(ctx, userID, g.ID); err != nil {
			err = xerrors.Errorf("Failed to API Client: %w", err)
			return nil, domain.ErrorInOtherAPI.New(err)
		}
	}

	return g, nil
}

func (gs *groupService) UploadThumbnail(ctx context.Context, data []byte) (string, error) {
	thumbnailURL, err := gs.groupUploader.UploadThumbnail(ctx, data)
	if err != nil {
		err = xerrors.Errorf("Failed to Uploader: %w", err)
		return "", domain.ErrorInStorage.New(err)
	}

	return thumbnailURL, nil
}

func (gs *groupService) ContainsUserID(ctx context.Context, g *group.Group, userID string) (bool, error) {
	if g == nil {
		err := xerrors.New("Group is empty")
		return false, domain.NotFound.New(err)
	}

	for _, v := range g.UserIDs {
		if v == userID {
			return true, nil
		}
	}

	return false, nil
}
