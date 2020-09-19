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
		apiV1.GET("/groups", reg.V1Group.Index)
		apiV1.POST("/groups", reg.V1Group.Create)
		apiV1.GET("/groups/:groupID", reg.V1Group.Show)
		apiV1.PATCH("/groups/:groupID", reg.V1Group.Update)

		groups := apiV1.Group("/groups/:groupID")
		{
			groups.POST("/users", reg.V1Group.AddUsers)
			groups.DELETE("/users", reg.V1Group.RemoveUsers)

			groups.GET("/payments", reg.V1Payment.Index)
			groups.POST("/payments", reg.V1Payment.Create)
			groups.PATCH("/payments/:paymentID", reg.V1Payment.Update)
			groups.DELETE("/payments/:paymentID", reg.V1Payment.Destroy)

			groups.PATCH("/payment-status", reg.V1Payment.UpdateStatusAll)
			groups.PATCH("/payment-status/:paymentID", reg.V1Payment.UpdateStatus)

			payments := groups.Group("/payments/:paymentID")
			{
				payments.PATCH("/payers/:payerID", reg.V1Payment.UpdatePayer)
			}
		}
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
