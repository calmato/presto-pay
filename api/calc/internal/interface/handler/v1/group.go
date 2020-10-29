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
	"golang.org/x/xerrors"
)

// APIV1GroupHandler - Groupハンドラのインターフェース
type APIV1GroupHandler interface {
	Index(ctx *gin.Context)
	Show(ctx *gin.Context)
	Create(ctx *gin.Context)
	Update(ctx *gin.Context)
	AddUsers(ctx *gin.Context)
	RemoveUsers(ctx *gin.Context)
	AddHiddenGroup(ctx *gin.Context)
	RemoveHiddenGroup(ctx *gin.Context)
	Destroy(ctx *gin.Context)
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
	gs, ghs, err := gh.groupApplication.Index(c)
	if err != nil {
		handler.ErrorHandling(ctx, err)
		return
	}

	grs := make([]*response.IndexGroup, len(gs))
	for i, g := range gs {
		gr := &response.IndexGroup{
			ID:           g.ID,
			Name:         g.Name,
			ThumbnailURL: g.ThumbnailURL,
			UserIDs:      g.UserIDs,
			CreatedAt:    g.CreatedAt,
			UpdatedAt:    g.UpdatedAt,
		}

		grs[i] = gr
	}

	ghrs := make([]*response.IndexGroup, len(ghs))
	for i, g := range ghs {
		gr := &response.IndexGroup{
			ID:           g.ID,
			Name:         g.Name,
			ThumbnailURL: g.ThumbnailURL,
			UserIDs:      g.UserIDs,
			CreatedAt:    g.CreatedAt,
			UpdatedAt:    g.UpdatedAt,
		}

		ghrs[i] = gr
	}

	res := &response.IndexGroups{
		Groups:       grs,
		HiddenGroups: ghrs,
	}

	ctx.JSON(http.StatusOK, res)
}

func (gh *apiV1GroupHandler) Show(ctx *gin.Context) {
	groupID := ctx.Params.ByName("groupID")

	c := middleware.GinContextToContext(ctx)
	g, err := gh.groupApplication.Show(c, groupID)
	if err != nil {
		handler.ErrorHandling(ctx, err)
		return
	}

	usr := make([]*response.UserToShowGroup, len(g.UserIDs))
	for i, userID := range g.UserIDs {
		u := g.Users[userID]
		if u == nil {
			err := xerrors.New("User is not found.")
			handler.ErrorHandling(ctx, domain.Unknown.New(err))
			return
		}

		ur := &response.UserToShowGroup{
			ID:           u.ID,
			Name:         u.Name,
			Username:     u.Username,
			Email:        u.Email,
			ThumbnailURL: u.ThumbnailURL,
		}

		usr[i] = ur
	}

	res := &response.ShowGroup{
		ID:           g.ID,
		Name:         g.Name,
		ThumbnailURL: g.ThumbnailURL,
		Users:        usr,
		CreatedAt:    g.CreatedAt,
		UpdatedAt:    g.UpdatedAt,
	}

	ctx.JSON(http.StatusOK, res)
}

func (gh *apiV1GroupHandler) Create(ctx *gin.Context) {
	req := &request.CreateGroup{}
	if err := ctx.BindJSON(req); err != nil {
		handler.ErrorHandling(ctx, domain.UnableParseJSON.New(err))
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

func (gh *apiV1GroupHandler) Update(ctx *gin.Context) {
	groupID := ctx.Params.ByName("groupID")

	req := &request.UpdateGroup{}
	if err := ctx.BindJSON(req); err != nil {
		handler.ErrorHandling(ctx, domain.UnableParseJSON.New(err))
		return
	}

	c := middleware.GinContextToContext(ctx)
	g, err := gh.groupApplication.Update(c, req, groupID)
	if err != nil {
		handler.ErrorHandling(ctx, err)
		return
	}

	res := &response.UpdateGroup{
		ID:           g.ID,
		Name:         g.Name,
		ThumbnailURL: g.ThumbnailURL,
		UserIDs:      g.UserIDs,
		CreatedAt:    g.CreatedAt,
		UpdatedAt:    g.UpdatedAt,
	}

	ctx.JSON(http.StatusOK, res)
}

func (gh *apiV1GroupHandler) AddUsers(ctx *gin.Context) {
	groupID := ctx.Params.ByName("groupID")

	req := &request.AddUsersInGroup{}
	if err := ctx.BindJSON(req); err != nil {
		handler.ErrorHandling(ctx, domain.UnableParseJSON.New(err))
		return
	}

	c := middleware.GinContextToContext(ctx)
	g, err := gh.groupApplication.AddUsers(c, req, groupID)
	if err != nil {
		handler.ErrorHandling(ctx, err)
		return
	}

	res := &response.AddUsersInGroup{
		ID:           g.ID,
		Name:         g.Name,
		ThumbnailURL: g.ThumbnailURL,
		UserIDs:      g.UserIDs,
		CreatedAt:    g.CreatedAt,
		UpdatedAt:    g.UpdatedAt,
	}

	ctx.JSON(http.StatusOK, res)
}

func (gh *apiV1GroupHandler) RemoveUsers(ctx *gin.Context) {
	groupID := ctx.Params.ByName("groupID")

	req := &request.RemoveUsersInGroup{}
	if err := ctx.BindJSON(req); err != nil {
		handler.ErrorHandling(ctx, domain.UnableParseJSON.New(err))
		return
	}

	c := middleware.GinContextToContext(ctx)
	g, err := gh.groupApplication.RemoveUsers(c, req, groupID)
	if err != nil {
		handler.ErrorHandling(ctx, err)
		return
	}

	res := &response.RemoveUsersInGroup{
		ID:           g.ID,
		Name:         g.Name,
		ThumbnailURL: g.ThumbnailURL,
		UserIDs:      g.UserIDs,
		CreatedAt:    g.CreatedAt,
		UpdatedAt:    g.UpdatedAt,
	}

	ctx.JSON(http.StatusOK, res)
}

func (gh *apiV1GroupHandler) AddHiddenGroup(ctx *gin.Context) {
	groupID := ctx.Params.ByName("groupID")

	c := middleware.GinContextToContext(ctx)
	gs, hgs, err := gh.groupApplication.AddHiddenGroup(c, groupID)
	if err != nil {
		handler.ErrorHandling(ctx, err)
		return
	}

	grs := make([]*response.IndexGroup, len(gs))
	for i, g := range gs {
		gr := &response.IndexGroup{
			ID:           g.ID,
			Name:         g.Name,
			ThumbnailURL: g.ThumbnailURL,
			UserIDs:      g.UserIDs,
			CreatedAt:    g.CreatedAt,
			UpdatedAt:    g.UpdatedAt,
		}

		grs[i] = gr
	}

	hgrs := make([]*response.IndexGroup, len(hgs))
	for i, g := range hgs {
		gr := &response.IndexGroup{
			ID:           g.ID,
			Name:         g.Name,
			ThumbnailURL: g.ThumbnailURL,
			UserIDs:      g.UserIDs,
			CreatedAt:    g.CreatedAt,
			UpdatedAt:    g.UpdatedAt,
		}

		hgrs[i] = gr
	}

	res := &response.IndexGroups{
		Groups:       grs,
		HiddenGroups: hgrs,
	}

	ctx.JSON(http.StatusOK, res)
}

func (gh *apiV1GroupHandler) RemoveHiddenGroup(ctx *gin.Context) {
	groupID := ctx.Params.ByName("groupID")

	c := middleware.GinContextToContext(ctx)
	gs, hgs, err := gh.groupApplication.RemoveHiddenGroup(c, groupID)
	if err != nil {
		handler.ErrorHandling(ctx, err)
		return
	}

	grs := make([]*response.IndexGroup, len(gs))
	for i, g := range gs {
		gr := &response.IndexGroup{
			ID:           g.ID,
			Name:         g.Name,
			ThumbnailURL: g.ThumbnailURL,
			UserIDs:      g.UserIDs,
			CreatedAt:    g.CreatedAt,
			UpdatedAt:    g.UpdatedAt,
		}

		grs[i] = gr
	}

	hgrs := make([]*response.IndexGroup, len(hgs))
	for i, g := range hgs {
		gr := &response.IndexGroup{
			ID:           g.ID,
			Name:         g.Name,
			ThumbnailURL: g.ThumbnailURL,
			UserIDs:      g.UserIDs,
			CreatedAt:    g.CreatedAt,
			UpdatedAt:    g.UpdatedAt,
		}

		hgrs[i] = gr
	}

	res := &response.IndexGroups{
		Groups:       grs,
		HiddenGroups: hgrs,
	}

	ctx.JSON(http.StatusOK, res)
}

func (gh *apiV1GroupHandler) Destroy(ctx *gin.Context) {
	groupID := ctx.Params.ByName("groupID")

	c := middleware.GinContextToContext(ctx)
	err := gh.groupApplication.Destroy(c, groupID)
	if err != nil {
		handler.ErrorHandling(ctx, err)
		return
	}

	ctx.JSON(http.StatusOK, gin.H{})
}
