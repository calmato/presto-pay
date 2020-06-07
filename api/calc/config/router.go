package config

import (
	"github.com/calmato/presto-pay/api/calc/internal/domain"
	"github.com/calmato/presto-pay/api/calc/internal/interface/handler"
	"github.com/calmato/presto-pay/api/calc/middleware"
	"github.com/calmato/presto-pay/api/calc/registry"
	"github.com/gin-gonic/gin"
	"github.com/prometheus/client_golang/prometheus/promhttp"
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

	// api v1 routes
	apiV1 := r.Group("/v1")
	{
		apiV1.POST("/groups", reg.V1Group.Create)
	}

	return r
}

// MetricsRouter - メトリクス公開用ルーティングの定義
func MetricsRouter() *gin.Engine {
	r := gin.Default()

	r.GET("/metrics", func(c *gin.Context) {
		h := promhttp.Handler()
		h.ServeHTTP(c.Writer, c.Request)
	})

	r.NoRoute(func(c *gin.Context) {
		err := xerrors.New("NotFound")
		handler.ErrorHandling(c, domain.NotFound.New(err))
	})

	return r
}
