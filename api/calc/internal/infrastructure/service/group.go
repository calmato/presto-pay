package service

import (
	"context"
	"time"

	"github.com/calmato/presto-pay/api/calc/internal/domain"
	"github.com/calmato/presto-pay/api/calc/internal/domain/group"
	"github.com/google/uuid"
	"golang.org/x/xerrors"
)

type groupService struct {
	groupDomainValidation group.GroupDomainValidation
	groupRepository       group.GroupRepository
	groupUploader         group.GroupUploader
}

// NewGroupService - GroupServiceの生成
func NewGroupService(
	gdv group.GroupDomainValidation, gr group.GroupRepository, gu group.GroupUploader,
) group.GroupService {
	return &groupService{
		groupDomainValidation: gdv,
		groupRepository:       gr,
		groupUploader:         gu,
	}
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

	// TODO: User.GroupIDsの更新

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
