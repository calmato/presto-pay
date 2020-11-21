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

func (gr *groupRepository) Show(ctx context.Context, groupID string) (*group.Group, error) {
	gc := getGroupCollection()

	doc, err := gr.firestore.Get(ctx, gc, groupID)
	if err != nil {
		return nil, err
	}

	g := &group.Group{}

	if err := doc.DataTo(g); err != nil {
		return nil, err
	}

	return g, nil
}

func (gr *groupRepository) Create(ctx context.Context, g *group.Group) error {
	gc := getGroupCollection()

	if err := gr.firestore.Set(ctx, gc, g.ID, g); err != nil {
		return err
	}

	return nil
}

func (gr *groupRepository) Update(ctx context.Context, g *group.Group) error {
	gc := getGroupCollection()

	if err := gr.firestore.Set(ctx, gc, g.ID, g); err != nil {
		return err
	}

	return nil
}

func (gr *groupRepository) Destroy(ctx context.Context, groupID string) error {
	gc := getGroupCollection()

	if err := gr.firestore.DeleteDoc(ctx, gc, groupID); err != nil {
		return err
	}

	return nil
}
