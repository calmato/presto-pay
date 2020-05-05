package registry

import (
	"github.com/calmato/presto-pay/api/user/internal/interface/handler"
)

// Registry - DIコンテナ
type Registry struct {
	Health handler.APIHealthHandler
}

// NewRegistry - internalディレクトリ配下のファイルを読み込み
func NewRegistry() *Registry {
	health := healthInjection()

	return &Registry{
		Health: health,
	}
}

func healthInjection() handler.APIHealthHandler {
	hh := handler.NewAPIHealthHandler()

	return hh
}
