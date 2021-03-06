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
		apiV1.GET("/auth", reg.V1User.ShowProfile)
		apiV1.POST("/auth", reg.V1User.Create)
		apiV1.GET("/auth/friends", reg.V1User.IndexFriends)
		apiV1.POST("/auth/friends", reg.V1User.AddFriend)
		apiV1.DELETE("/auth/friends/:userID", reg.V1User.RemoveFriend)
		apiV1.PATCH("/auth", reg.V1User.UpdateProfile)
		apiV1.PATCH("/auth/password", reg.V1User.UpdatePassword)
		apiV1.POST("/auth/device", reg.V1User.RegisterInstanceID)

		apiV1.GET("/users", reg.V1User.IndexByUsername)
		apiV1.GET("/users/:userID", reg.V1User.Show)

		apiV1.POST("/users/check-email", reg.V1User.UniqueCheckEmail)
		apiV1.POST("/users/check-username", reg.V1User.UniqueCheckUsername)

		apiV1.PATCH("/users/:userID/unauthorized-user", reg.V1User.UpdateUnauthorizedUser)
	}

	// internal routes
	internal := r.Group("/internal")
	{
		internal.GET("/users/:userID", reg.V1User.ShowInternal)

		internal.POST("/users/:userID/groups/:groupID", reg.V1User.AddGroup)
		internal.DELETE("/users/:userID/groups/:groupID", reg.V1User.RemoveGroup)

		internal.POST("/unauthorized-users", reg.V1User.CreateUnauthorizedUser)

		internal.POST("/groups/:groupID", reg.V1User.AddHiddenGroup)
		internal.DELETE("/groups/:groupID", reg.V1User.RemoveHiddenGroup)
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
