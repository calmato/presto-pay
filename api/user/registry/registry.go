package registry

import (
	"github.com/calmato/presto-pay/api/user/internal/interface/handler"
	"github.com/calmato/presto-pay/api/user/lib/firebase/authentication"
	"github.com/calmato/presto-pay/api/user/lib/firebase/firestore"
	"github.com/calmato/presto-pay/api/user/lib/firebase/storage"
)

// Registry - DIコンテナ
type Registry struct {
	Health handler.APIHealthHandler
}

// NewRegistry - internalディレクトリ配下のファイルを読み込み
func NewRegistry(_ *authentication.Auth, _ *firestore.Firestore, cs *storage.Storage) *Registry {
	health := healthInjection()

	return &Registry{
		Health: health,
	}
}

func healthInjection() handler.APIHealthHandler {
	hh := handler.NewAPIHealthHandler()

	return hh
}
