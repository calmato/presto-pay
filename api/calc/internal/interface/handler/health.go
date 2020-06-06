package handler

import (
	"net/http"

	"github.com/gin-gonic/gin"
)

// APIHealthHandler - ヘルスチェックハンドラ
type APIHealthHandler interface {
	HealthCheck(ctx *gin.Context)
}

type apiHealthHandler struct{}

// NewAPIHealthHandler - apiHealthHandlerの生成
func NewAPIHealthHandler() APIHealthHandler {
	return &apiHealthHandler{}
}

// HealthCheck - ヘルスチェック
func (hh *apiHealthHandler) HealthCheck(ctx *gin.Context) {
	ctx.JSON(http.StatusOK, gin.H{"status": "ok"})
}
