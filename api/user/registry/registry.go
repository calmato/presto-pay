package registry

import (
	"github.com/calmato/presto-pay/api/user/internal/interface/handler"
	v1 "github.com/calmato/presto-pay/api/user/internal/interface/handler/v1"
	"github.com/calmato/presto-pay/api/user/lib/firebase/authentication"
	"github.com/calmato/presto-pay/api/user/lib/firebase/firestore"
	"github.com/calmato/presto-pay/api/user/lib/firebase/storage"
)

// Registry - DIコンテナ
type Registry struct {
	Health handler.APIHealthHandler
	V1User v1.APIV1UserHandler
}

// NewRegistry - internalディレクトリ配下のファイルを読み込み
func NewRegistry(fa *authentication.Auth, fs *firestore.Firestore, cs *storage.Storage) *Registry {
	health := healthInjection()
	v1User := v1UserInjection(fa, fs, cs)

	return &Registry{
		Health: health,
		V1User: v1User,
	}
}

func healthInjection() handler.APIHealthHandler {
	hh := handler.NewAPIHealthHandler()

	return hh
}
