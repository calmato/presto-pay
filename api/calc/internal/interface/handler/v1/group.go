package v1

import (
	"net/http"

	"github.com/calmato/presto-pay/api/calc/internal/application"
	"github.com/calmato/presto-pay/api/calc/internal/application/request"
	"github.com/calmato/presto-pay/api/calc/internal/application/response"
	"github.com/calmato/presto-pay/api/calc/internal/domain"
	"github.com/calmato/presto-pay/api/calc/internal/interface/handler"
	"github.com/calmato/presto-pay/api/calc/middleware"
	"github.com/gin-gonic/gin"
)

// APIV1GroupHandler - Groupハンドラのインターフェース
type APIV1GroupHandler interface {
	Index(ctx *gin.Context)
	Create(ctx *gin.Context)
}

type apiV1GroupHandler struct {
	groupApplication application.GroupApplication
}

// NewAPIV1GroupHandler - APIV1GroupHandlerの生成
func NewAPIV1GroupHandler(ga application.GroupApplication) APIV1GroupHandler {
	return &apiV1GroupHandler{
		groupApplication: ga,
	}
}

func (gh *apiV1GroupHandler) Index(ctx *gin.Context) {
	c := middleware.GinContextToContext(ctx)
	gs, err := gh.groupApplication.Index(c)
	if err != nil {
		handler.ErrorHandling(ctx, err)
		return
	}

	res := &response.IndexGroups{}

	for i, g := range gs {
		urs := make([]*response.UserToShowGroup, len(g.Users))
		for j, u := range g.Users {
			ur := &response.UserToShowGroup{
				ID:           u.ID,
				Name:         u.Name,
				Username:     u.Username,
				Email:        u.Email,
				ThumbnailURL: u.ThumbnailURL,
			}

			urs[j] = ur
		}

		gr := &response.ShowGroup{
			ID:           g.ID,
			Name:         g.Name,
			ThumbnailURL: g.ThumbnailURL,
			Users:        urs,
			CreatedAt:    g.CreatedAt,
			UpdatedAt:    g.UpdatedAt,
		}

		res.Groups[i] = gr
	}

	ctx.JSON(http.StatusOK, res)
}

func (gh *apiV1GroupHandler) Create(ctx *gin.Context) {
	req := &request.CreateGroup{}
	if err := ctx.BindJSON(req); err != nil {
		handler.ErrorHandling(ctx, domain.Unauthorized.New(err))
		return
	}

	c := middleware.GinContextToContext(ctx)
	g, err := gh.groupApplication.Create(c, req)
	if err != nil {
		handler.ErrorHandling(ctx, err)
		return
	}

	res := &response.CreateGroup{
		ID:           g.ID,
		Name:         g.Name,
		ThumbnailURL: g.ThumbnailURL,
		UserIDs:      g.UserIDs,
		CreatedAt:    g.CreatedAt,
		UpdatedAt:    g.UpdatedAt,
	}

	ctx.JSON(http.StatusOK, res)
}
