package config

import (
	"github.com/gin-gonic/gin"
)

// Router - ルーティングの定義
func Router() *gin.Engine {
	r := gin.Default()

	// TODO: Cors設定

	// TODO: Logging設定

	// TODO: health checkのエンドポイント実装

	return r
}
