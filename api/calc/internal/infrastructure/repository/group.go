package repository

import (
	"context"

	"github.com/calmato/presto-pay/api/calc/internal/domain/group"
	"github.com/calmato/presto-pay/api/calc/lib/firebase/firestore"
)

type groupRepository struct {
	firestore *firestore.Firestore
}

// NewGroupRepository - GroupRepositoryの生成
func NewGroupRepository(fs *firestore.Firestore) group.GroupRepository {
	return &groupRepository{
		firestore: fs,
	}
}

func (gr *groupRepository) Create(ctx context.Context, g *group.Group) error {
	groupCollection := getGroupCollection()

	if err := gr.firestore.Set(ctx, groupCollection, g.ID, g); err != nil {
		return err
	}

	// TODO: UserIDsのユニーク検証

	// TODO: UserIDの存在性検証

	return nil
}
