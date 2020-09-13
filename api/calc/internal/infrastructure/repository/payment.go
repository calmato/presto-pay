package repository

import (
	"context"

	"github.com/calmato/presto-pay/api/calc/internal/domain/payment"
	"github.com/calmato/presto-pay/api/calc/lib/firebase/firestore"
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
	paymentCollection := getPaymentCollection(groupID)

	docs, err := pr.firestore.GetAllFirst(ctx, paymentCollection, 50)
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

func (pr *paymentRepository) Create(ctx context.Context, p *payment.Payment, groupID string) error {
	paymentCollection := getPaymentCollection(groupID)

	if err := pr.firestore.Set(ctx, paymentCollection, p.ID, p); err != nil {
		return err
	}

	return nil
}
