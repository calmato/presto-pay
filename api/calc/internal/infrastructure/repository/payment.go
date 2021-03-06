package repository

import (
	"context"

	"github.com/calmato/presto-pay/api/calc/internal/domain/payment"
	"github.com/calmato/presto-pay/api/calc/lib/firebase/firestore"
	"google.golang.org/api/iterator"
)

type paymentRepository struct {
	firestore *firestore.Firestore
}

// NewPaymentRepository - PaymentRepositoryの生成
func NewPaymentRepository(fs *firestore.Firestore) payment.PaymentRepository {
	return &paymentRepository{
		firestore: fs,
	}
}

func (pr *paymentRepository) Index(ctx context.Context, groupID string) ([]*payment.Payment, error) {
	pc := getPaymentCollection(groupID)

	docs, err := pr.firestore.GetAllFirst(ctx, pc, "created_at", "desc", 50)
	if err != nil {
		return nil, err
	}

	ps := make([]*payment.Payment, len(docs))

	for i, doc := range docs {
		p := &payment.Payment{}

		if err = doc.DataTo(p); err != nil {
			return nil, err
		}

		ps[i] = p
	}

	return ps, nil
}

func (pr *paymentRepository) IndexFromStartAt(
	ctx context.Context, groupID string, startAt string,
) ([]*payment.Payment, error) {
	pc := getPaymentCollection(groupID)

	docs, err := pr.firestore.GetAllFromStartAt(ctx, pc, "created_at", "desc", startAt, 50)
	if err != nil {
		return nil, err
	}

	ps := make([]*payment.Payment, len(docs))

	for i, doc := range docs {
		p := &payment.Payment{}

		if err = doc.DataTo(p); err != nil {
			return nil, err
		}

		ps[i] = p
	}

	return ps, nil
}

func (pr *paymentRepository) IndexByIsCompleted(
	ctx context.Context, groupID string, isCompleted bool,
) ([]*payment.Payment, error) {
	pc := getPaymentCollection(groupID)

	q := &firestore.Query{
		Field:    "is_completed",
		Operator: "==",
		Value:    isCompleted,
	}

	ps := make([]*payment.Payment, 0)

	iter := pr.firestore.GetByQuery(ctx, pc, q)
	for {
		doc, err := iter.Next()
		if err == iterator.Done {
			break
		}

		p := &payment.Payment{}

		err = doc.DataTo(p)
		if err != nil {
			return nil, err
		}

		ps = append(ps, p)
	}

	return ps, nil
}

func (pr *paymentRepository) Show(ctx context.Context, groupID string, paymentID string) (*payment.Payment, error) {
	pc := getPaymentCollection(groupID)

	doc, err := pr.firestore.Get(ctx, pc, paymentID)
	if err != nil {
		return nil, err
	}

	p := &payment.Payment{}

	if err = doc.DataTo(p); err != nil {
		return nil, err
	}

	return p, nil
}

func (pr *paymentRepository) Create(ctx context.Context, p *payment.Payment, groupID string) error {
	pc := getPaymentCollection(groupID)

	if err := pr.firestore.Set(ctx, pc, p.ID, p); err != nil {
		return err
	}

	return nil
}

func (pr *paymentRepository) Update(ctx context.Context, p *payment.Payment, groupID string) error {
	pc := getPaymentCollection(groupID)

	if err := pr.firestore.Set(ctx, pc, p.ID, p); err != nil {
		return err
	}

	return nil
}

func (pr *paymentRepository) Destroy(ctx context.Context, groupID string, paymentID string) error {
	pc := getPaymentCollection(groupID)

	if err := pr.firestore.DeleteDoc(ctx, pc, paymentID); err != nil {
		return err
	}

	return nil
}
