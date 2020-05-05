package config

import (
	"github.com/calmato/presto-pay/api/user/registry"
	"github.com/gin-gonic/gin"
)

// Router - ルーティングの定義
func Router(reg *registry.Registry) *gin.Engine {
	r := gin.Default()

	// Cors設定
	r.Use(SetCors())

	// TODO: Logging設定

	r.GET("/health", reg.Health.HealthCheck)

	return r
}
