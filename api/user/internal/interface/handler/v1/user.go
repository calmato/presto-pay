package v1

import (
	"net/http"

	"github.com/calmato/presto-pay/api/user/internal/application"
	"github.com/calmato/presto-pay/api/user/internal/application/request"
	"github.com/calmato/presto-pay/api/user/internal/domain"
	"github.com/calmato/presto-pay/api/user/internal/interface/handler"
	"github.com/calmato/presto-pay/api/user/middleware"
	"github.com/gin-gonic/gin"
)

// APIV1UserHandler - Userハンドラのインターフェース
type APIV1UserHandler interface {
	Create(ctx *gin.Context)
}

type apiV1UserHandler struct {
	userApplication application.UserApplication
}

// NewAPIV1UserHandler - APIV1UserHandlerの生成
func NewAPIV1UserHandler(ua application.UserApplication) APIV1UserHandler {
	return &apiV1UserHandler{
		userApplication: ua,
	}
}

func (uh *apiV1UserHandler) Create(ctx *gin.Context) {
	req := &request.CreateUser{}
	if err := ctx.BindJSON(req); err != nil {
		handler.ErrorHandling(ctx, domain.UnableParseJSON.New(err))
		return
	}

	c := middleware.GinContextToContext(ctx)
	if err := uh.userApplication.Create(c, req); err != nil {
		handler.ErrorHandling(ctx, err)
		return
	}

	ctx.JSON(http.StatusOK, gin.H{})
}
