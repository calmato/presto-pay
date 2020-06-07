package config

import (
	"github.com/calmato/presto-pay/api/user/internal/domain"
	"github.com/calmato/presto-pay/api/user/internal/interface/handler"
	"github.com/calmato/presto-pay/api/user/middleware"
	"github.com/calmato/presto-pay/api/user/registry"
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
		apiV1.GET("/users", reg.V1User.ShowProfile)
		apiV1.POST("/users", reg.V1User.Create)
		apiV1.PATCH("/users", reg.V1User.UpdateProfile)

		apiV1.PATCH("/users/password", reg.V1User.UpdatePassword)

		apiV1.POST("/users/check-email", reg.V1User.UniqueCheckEmail)
		apiV1.POST("/users/check-username", reg.V1User.UniqueCheckUsername)
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
