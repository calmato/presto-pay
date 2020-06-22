package registry

import (
	"github.com/calmato/presto-pay/api/calc/internal/infrastructure/api"
	"github.com/calmato/presto-pay/api/calc/internal/interface/handler"
	v1 "github.com/calmato/presto-pay/api/calc/internal/interface/handler/v1"
	"github.com/calmato/presto-pay/api/calc/lib/firebase/firestore"
	"github.com/calmato/presto-pay/api/calc/lib/firebase/storage"
)

// Registry - DIコンテナ
type Registry struct {
	Health  handler.APIHealthHandler
	V1Group v1.APIV1GroupHandler
}

// NewRegistry - internalディレクトリ配下のファイルを読み込み
func NewRegistry(fs *firestore.Firestore, cs *storage.Storage, ac api.APIClient) *Registry {
	health := healthInjection()
	v1Group := v1GroupInjection(fs, cs, ac)

	return &Registry{
		Health:  health,
		V1Group: v1Group,
	}
}

func healthInjection() handler.APIHealthHandler {
	hh := handler.NewAPIHealthHandler()

	return hh
}
