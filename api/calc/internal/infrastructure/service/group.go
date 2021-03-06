package service

import (
	"context"
	"time"

	"github.com/calmato/presto-pay/api/calc/internal/domain"
	"github.com/calmato/presto-pay/api/calc/internal/domain/group"
	"github.com/calmato/presto-pay/api/calc/internal/domain/user"
	"github.com/calmato/presto-pay/api/calc/internal/infrastructure/api"
	"github.com/calmato/presto-pay/api/calc/internal/infrastructure/notification"
	"github.com/calmato/presto-pay/api/calc/lib/common"
	"github.com/google/uuid"
	"golang.org/x/xerrors"
)

type groupService struct {
	groupDomainValidation group.GroupDomainValidation
	groupRepository       group.GroupRepository
	groupUploader         group.GroupUploader
	apiClient             api.APIClient
	notificationClient    notification.NotificationClient
}

// NewGroupService - GroupServiceの生成
func NewGroupService(
	gdv group.GroupDomainValidation, gr group.GroupRepository, gu group.GroupUploader,
	ac api.APIClient, nc notification.NotificationClient,
) group.GroupService {
	return &groupService{
		groupDomainValidation: gdv,
		groupRepository:       gr,
		groupUploader:         gu,
		apiClient:             ac,
		notificationClient:    nc,
	}
}

func (gs *groupService) Index(ctx context.Context, u *user.User) ([]*group.Group, []*group.Group, error) {
	groups := make([]*group.Group, 0)
	hiddenGroups := make([]*group.Group, 0)

	for _, groupID := range u.GroupIDs {
		g, err := gs.groupRepository.Show(ctx, groupID)
		if err != nil {
			return nil, nil, domain.ErrorInDatastore.New(err)
		}

		// 非公開グループ設定がされている場合、groupsから削除
		if containsHiddenGroupIDs(u, g.ID) {
			hiddenGroups = append(hiddenGroups, g)
		} else {
			groups = append(groups, g)
		}
	}

	return groups, hiddenGroups, nil
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
			return nil, err
		}

		us[userID] = u
	}

	g.Users = us

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

	deviceTokens := []string{}
	for _, userID := range g.UserIDs {
		u, err := gs.apiClient.AddGroup(ctx, userID, g.ID)
		if err != nil {
			return nil, err
		}

		if u == nil || u.InstanceID == "" {
			continue
		}

		deviceTokens = append(deviceTokens, u.InstanceID)
	}

	if len(deviceTokens) > 0 {
		if err := gs.notificationClient.Send(ctx, deviceTokens, g.Name, domain.CreateGroupNotification); err != nil {
			err = xerrors.Errorf("Failed to Firebase Cloud Messaging: %w", err)
			return nil, domain.Unknown.New(err)
		}
	}

	return g, nil
}

func (gs *groupService) Update(ctx context.Context, g *group.Group, userIDs []string) (*group.Group, error) {
	if ves := gs.groupDomainValidation.Group(ctx, g); len(ves) > 0 {
		err := xerrors.New("Failed to DomainValidation")
		return nil, domain.Unknown.New(err, ves...)
	}

	// Add Users
	addUserIDs := make([]string, 0)
	for _, userID := range userIDs {
		if containsUserID(g.UserIDs, userID) {
			continue
		}

		addUserIDs = append(addUserIDs, userID)
		g.UserIDs = append(g.UserIDs, userID)
	}

	// Remove Users
	removeUserIDs := make([]string, 0)
	for _, userID := range g.UserIDs {
		if containsUserID(userIDs, userID) {
			continue
		}

		removeUserIDs = append(removeUserIDs, userID)
		g.UserIDs = common.RemoveString(g.UserIDs, userID)
	}

	// Update Group
	current := time.Now()
	g.UpdatedAt = current

	if err := gs.groupRepository.Update(ctx, g); err != nil {
		return nil, domain.ErrorInDatastore.New(err)
	}

	// Update Users (add)
	for _, userID := range addUserIDs {
		_, _ = gs.apiClient.AddGroup(ctx, userID, g.ID)
	}

	// Update Users (remove)
	for _, userID := range removeUserIDs {
		_, _ = gs.apiClient.RemoveGroup(ctx, userID, g.ID)
	}

	deviceTokens := []string{}
	for _, userID := range g.UserIDs {
		u, _ := gs.apiClient.ShowUser(ctx, userID)
		if u == nil || u.InstanceID == "" {
			continue
		}

		deviceTokens = append(deviceTokens, u.InstanceID)
	}

	if err := gs.notificationClient.Send(ctx, deviceTokens, g.Name, domain.UpdateGroupNotification); err != nil {
		err = xerrors.Errorf("Failed to Firebase Cloud Messaging: %w", err)
		return nil, domain.Unknown.New(err)
	}

	return g, nil
}

func (gs *groupService) AddUsers(
	ctx context.Context, groupID string, userIDs []string,
) (*group.Group, error) {
	g, err := gs.groupRepository.Show(ctx, groupID)
	if err != nil {
		return nil, domain.ErrorInDatastore.New(err)
	}

	for _, userID := range userIDs {
		if containsUserID(g.UserIDs, userID) {
			err := xerrors.New("Failed to Servicee")
			return nil, domain.AlreadyExistsInDatastore.New(err)
		}

		g.UserIDs = append(g.UserIDs, userID)
	}

	if ves := gs.groupDomainValidation.Group(ctx, g); len(ves) > 0 {
		err := xerrors.New("Failed to DomainValidation")
		return nil, domain.InvalidDomainValidation.New(err, ves...)
	}

	// Update Group
	current := time.Now()
	g.UpdatedAt = current

	if err := gs.groupRepository.Update(ctx, g); err != nil {
		return nil, domain.ErrorInDatastore.New(err)
	}

	// Update Users (add)
	deviceTokens := []string{}
	for _, userID := range userIDs {
		u, _ := gs.apiClient.AddGroup(ctx, userID, g.ID)
		if u == nil || u.InstanceID == "" {
			continue
		}

		deviceTokens = append(deviceTokens, u.InstanceID)
	}

	if err := gs.notificationClient.Send(ctx, deviceTokens, g.Name, domain.AddUserInGroupNotification); err != nil {
		err = xerrors.Errorf("Failed to Firebase Cloud Messaging: %w", err)
		return nil, domain.Unknown.New(err)
	}

	return g, nil
}

func (gs *groupService) RemoveUsers(
	ctx context.Context, groupID string, userIDs []string,
) (*group.Group, error) {
	g, err := gs.groupRepository.Show(ctx, groupID)
	if err != nil {
		return nil, domain.ErrorInDatastore.New(err)
	}

	for _, userID := range userIDs {
		if !containsUserID(g.UserIDs, userID) {
			err := xerrors.New("Failed to Servicee")
			return nil, domain.NotEqualRequestWithDatastore.New(err)
		}

		g.UserIDs = common.RemoveString(g.UserIDs, userID)
	}

	if ves := gs.groupDomainValidation.Group(ctx, g); len(ves) > 0 {
		err := xerrors.New("Failed to DomainValidation")
		return nil, domain.InvalidDomainValidation.New(err, ves...)
	}

	// Update Group
	current := time.Now()
	g.UpdatedAt = current

	if err := gs.groupRepository.Update(ctx, g); err != nil {
		return nil, domain.ErrorInDatastore.New(err)
	}

	// Update Users (add)
	for _, userID := range userIDs {
		_, _ = gs.apiClient.RemoveGroup(ctx, userID, g.ID)
	}

	return g, nil
}

func (gs *groupService) AddHiddenGroup(
	ctx context.Context, groupID string,
) ([]*group.Group, []*group.Group, error) {
	u, err := gs.apiClient.AddHiddenGroup(ctx, groupID)
	if err != nil {
		return nil, nil, err
	}

	groups := make([]*group.Group, 0)
	hiddenGroups := make([]*group.Group, 0)

	for _, groupID := range u.GroupIDs {
		g, err := gs.groupRepository.Show(ctx, groupID)
		if err != nil {
			return nil, nil, domain.ErrorInDatastore.New(err)
		}

		// 非公開グループ設定がされている場合、groupsから削除
		if containsHiddenGroupIDs(u, g.ID) {
			hiddenGroups = append(hiddenGroups, g)
		} else {
			groups = append(groups, g)
		}
	}

	return groups, hiddenGroups, nil
}

func (gs *groupService) RemoveHiddenGroup(
	ctx context.Context, groupID string,
) ([]*group.Group, []*group.Group, error) {
	u, err := gs.apiClient.RemoveHiddenGroup(ctx, groupID)
	if err != nil {
		return nil, nil, err
	}

	groups := make([]*group.Group, 0)
	hiddenGroups := make([]*group.Group, 0)

	for _, groupID := range u.GroupIDs {
		g, err := gs.groupRepository.Show(ctx, groupID)
		if err != nil {
			return nil, nil, domain.ErrorInDatastore.New(err)
		}

		// 非公開グループ設定がされている場合、groupsから削除
		if containsHiddenGroupIDs(u, g.ID) {
			hiddenGroups = append(hiddenGroups, g)
		} else {
			groups = append(groups, g)
		}
	}

	return groups, hiddenGroups, nil
}

func (gs *groupService) Destroy(ctx context.Context, groupID string) error {
	g, err := gs.groupRepository.Show(ctx, groupID)
	if err != nil {
		return domain.ErrorInDatastore.New(err)
	}

	for _, userID := range g.UserIDs {
		_, _ = gs.apiClient.RemoveGroup(ctx, userID, g.ID)
	}

	if err := gs.groupRepository.Destroy(ctx, groupID); err != nil {
		err = xerrors.Errorf("Failed to Repository: %w", err)
		return domain.ErrorInDatastore.New(err)
	}

	return nil
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

func containsUserID(userIDs []string, userID string) bool {
	if userIDs == nil {
		return false
	}

	for _, v := range userIDs {
		if v == userID {
			return true
		}
	}

	return false
}

func containsHiddenGroupIDs(u *user.User, groupID string) bool {
	if u == nil {
		return false
	}

	for _, hiddenGroupID := range u.HiddenGroupIDs {
		if hiddenGroupID == groupID {
			return true
		}
	}

	return false
}
