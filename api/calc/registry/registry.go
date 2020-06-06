package registry

import (
	"github.com/calmato/presto-pay/api/calc/internal/interface/handler"
	"github.com/calmato/presto-pay/api/calc/lib/firebase/authentication"
	"github.com/calmato/presto-pay/api/calc/lib/firebase/firestore"
	"github.com/calmato/presto-pay/api/calc/lib/firebase/storage"
)

// Registry - DIコンテナ
type Registry struct {
	Health handler.APIHealthHandler
}

// NewRegistry - internalディレクトリ配下のファイルを読み込み
func NewRegistry(fa *authentication.Auth, fs *firestore.Firestore, cs *storage.Storage) *Registry {
	health := healthInjection()

	return &Registry{
		Health: health,
	}
}

func healthInjection() handler.APIHealthHandler {
	hh := handler.NewAPIHealthHandler()

	return hh
}
