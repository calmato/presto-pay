package registry

import (
	"github.com/calmato/presto-pay/api/calc/internal/infrastructure/api"
	"github.com/calmato/presto-pay/api/calc/internal/interface/handler"
	v1 "github.com/calmato/presto-pay/api/calc/internal/interface/handler/v1"
	"github.com/calmato/presto-pay/api/calc/lib/firebase/firestore"
	"github.com/calmato/presto-pay/api/calc/lib/firebase/messaging"
	"github.com/calmato/presto-pay/api/calc/lib/firebase/storage"
)

// Registry - DIコンテナ
type Registry struct {
	Health    handler.APIHealthHandler
	V1Group   v1.APIV1GroupHandler
	V1Payment v1.APIV1PaymentHandler
}

// NewRegistry - internalディレクトリ配下のファイルを読み込み
func NewRegistry(fs *firestore.Firestore, cs *storage.Storage, _ *messaging.Messaging, ac api.APIClient) *Registry {
	health := healthInjection()
	v1Group := v1GroupInjection(fs, cs, ac)
	v1Payment := v1PaymentInjection(fs, cs, ac)

	return &Registry{
		Health:    health,
		V1Group:   v1Group,
		V1Payment: v1Payment,
	}
}

func healthInjection() handler.APIHealthHandler {
	hh := handler.NewAPIHealthHandler()

	return hh
}
