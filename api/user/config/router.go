package config

import (
	"github.com/calmato/presto-pay/api/user/internal/domain"
	"github.com/calmato/presto-pay/api/user/internal/interface/handler"
	"github.com/calmato/presto-pay/api/user/middleware"
	"github.com/calmato/presto-pay/api/user/registry"
	"github.com/gin-gonic/gin"
	"golang.org/x/xerrors"
)

// Router - ルーティングの定義
func Router(reg *registry.Registry) *gin.Engine {
	r := gin.Default()

	// Cors設定
	r.Use(SetCors())

	// Logging設定
	r.Use(middleware.Logging())

	r.GET("/health", reg.Health.HealthCheck)

	r.NoRoute(func(c *gin.Context) {
		err := xerrors.New("NotFound")
		handler.ErrorHandling(c, domain.NotFound.New(err))
	})

	return r
}
