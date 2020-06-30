package v1

import (
	"net/http"

	"github.com/calmato/presto-pay/api/user/internal/application"
	"github.com/calmato/presto-pay/api/user/internal/application/request"
	"github.com/calmato/presto-pay/api/user/internal/application/response"
	"github.com/calmato/presto-pay/api/user/internal/domain"
	"github.com/calmato/presto-pay/api/user/internal/interface/handler"
	"github.com/calmato/presto-pay/api/user/middleware"
	"github.com/gin-gonic/gin"
)

// APIV1UserHandler - Userハンドラのインターフェース
type APIV1UserHandler interface {
	IndexByUsername(ctx *gin.Context)
	Show(ctx *gin.Context)
	ShowProfile(ctx *gin.Context)
	Create(ctx *gin.Context)
	UpdateProfile(ctx *gin.Context)
	UpdatePassword(ctx *gin.Context)
	UniqueCheckEmail(ctx *gin.Context)
	UniqueCheckUsername(ctx *gin.Context)
	AddGroup(ctx *gin.Context)
	RemoveGroup(ctx *gin.Context)
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

func (uh *apiV1UserHandler) IndexByUsername(ctx *gin.Context) {
	username := ctx.DefaultQuery("username", "")
	startAt := ctx.DefaultQuery("after", "")

	req := &request.IndexByUsername{
		Username: username,
		StartAt:  startAt,
	}

	c := middleware.GinContextToContext(ctx)
	us, err := uh.userApplication.IndexByUsername(c, req)
	if err != nil {
		handler.ErrorHandling(ctx, err)
		return
	}

	res := &response.IndexUsers{
		Users: []*response.ShowUser{},
	}

	for _, u := range us {
		ur := &response.ShowUser{
			ID:           u.ID,
			Name:         u.Name,
			Username:     u.Username,
			Email:        u.Email,
			ThumbnailURL: u.ThumbnailURL,
			GroupIDs:     u.GroupIDs,
		}

		res.Users = append(res.Users, ur)
	}

	ctx.JSON(http.StatusOK, res)
}

func (uh *apiV1UserHandler) Show(ctx *gin.Context) {
	userID := ctx.Params.ByName("userID")

	c := middleware.GinContextToContext(ctx)
	u, err := uh.userApplication.Show(c, userID)
	if err != nil {
		handler.ErrorHandling(ctx, err)
		return
	}

	res := &response.ShowUser{
		ID:           u.ID,
		Name:         u.Name,
		Username:     u.Username,
		Email:        u.Email,
		ThumbnailURL: u.ThumbnailURL,
		GroupIDs:     u.GroupIDs,
	}

	ctx.JSON(http.StatusOK, res)
}

func (uh *apiV1UserHandler) ShowProfile(ctx *gin.Context) {
	c := middleware.GinContextToContext(ctx)
	u, err := uh.userApplication.ShowProfile(c)
	if err != nil {
		handler.ErrorHandling(ctx, err)
		return
	}

	res := &response.ShowProfile{
		ID:           u.ID,
		Name:         u.Name,
		Username:     u.Username,
		Email:        u.Email,
		ThumbnailURL: u.ThumbnailURL,
		GroupIDs:     u.GroupIDs,
		CreatedAt:    u.CreatedAt,
		UpdatedAt:    u.UpdatedAt,
	}

	ctx.JSON(http.StatusOK, res)
}

func (uh *apiV1UserHandler) Create(ctx *gin.Context) {
	req := &request.CreateUser{}
	if err := ctx.BindJSON(req); err != nil {
		handler.ErrorHandling(ctx, domain.UnableParseJSON.New(err))
		return
	}

	c := middleware.GinContextToContext(ctx)
	u, err := uh.userApplication.Create(c, req)
	if err != nil {
		handler.ErrorHandling(ctx, err)
		return
	}

	res := &response.CreateUser{
		ID:           u.ID,
		Name:         u.Name,
		Username:     u.Username,
		Email:        u.Email,
		ThumbnailURL: u.ThumbnailURL,
		GroupIDs:     u.GroupIDs,
		CreatedAt:    u.CreatedAt,
		UpdatedAt:    u.UpdatedAt,
	}

	ctx.JSON(http.StatusOK, res)
}

func (uh *apiV1UserHandler) UpdateProfile(ctx *gin.Context) {
	req := &request.UpdateProfile{}
	if err := ctx.BindJSON(req); err != nil {
		handler.ErrorHandling(ctx, domain.UnableParseJSON.New(err))
		return
	}

	c := middleware.GinContextToContext(ctx)
	u, err := uh.userApplication.UpdateProfile(c, req)
	if err != nil {
		handler.ErrorHandling(ctx, err)
		return
	}

	res := &response.UpdateProfile{
		ID:           u.ID,
		Name:         u.Name,
		Username:     u.Username,
		Email:        u.Email,
		ThumbnailURL: u.ThumbnailURL,
		GroupIDs:     u.GroupIDs,
		CreatedAt:    u.CreatedAt,
		UpdatedAt:    u.UpdatedAt,
	}

	ctx.JSON(http.StatusOK, res)
}

func (uh *apiV1UserHandler) UpdatePassword(ctx *gin.Context) {
	req := &request.UpdateUserPassword{}
	if err := ctx.BindJSON(req); err != nil {
		handler.ErrorHandling(ctx, domain.UnableParseJSON.New(err))
		return
	}

	c := middleware.GinContextToContext(ctx)
	_, err := uh.userApplication.UpdatePassword(c, req)
	if err != nil {
		handler.ErrorHandling(ctx, err)
		return
	}

	ctx.JSON(http.StatusOK, gin.H{})
}

func (uh *apiV1UserHandler) UniqueCheckEmail(ctx *gin.Context) {
	req := &request.UniqueCheckUserEmail{}
	if err := ctx.BindJSON(req); err != nil {
		handler.ErrorHandling(ctx, domain.UnableParseJSON.New(err))
		return
	}

	c := middleware.GinContextToContext(ctx)
	uniqueCheck, err := uh.userApplication.UniqueCheckEmail(c, req)
	if err != nil {
		handler.ErrorHandling(ctx, err)
		return
	}

	res := &response.UniqueCheck{
		UniqueCheck: uniqueCheck,
	}

	ctx.JSON(http.StatusOK, res)
}

func (uh *apiV1UserHandler) UniqueCheckUsername(ctx *gin.Context) {
	req := &request.UniqueCheckUserUsername{}
	if err := ctx.BindJSON(req); err != nil {
		handler.ErrorHandling(ctx, domain.UnableParseJSON.New(err))
		return
	}

	c := middleware.GinContextToContext(ctx)
	uniqueCheck, err := uh.userApplication.UniqueCheckUsername(c, req)
	if err != nil {
		handler.ErrorHandling(ctx, err)
		return
	}

	res := &response.UniqueCheck{
		UniqueCheck: uniqueCheck,
	}

	ctx.JSON(http.StatusOK, res)
}

func (uh *apiV1UserHandler) AddGroup(ctx *gin.Context) {
	userID := ctx.Params.ByName("userID")
	groupID := ctx.Params.ByName("groupID")

	c := middleware.GinContextToContext(ctx)
	u, err := uh.userApplication.AddGroupID(c, userID, groupID)
	if err != nil {
		handler.ErrorHandling(ctx, err)
		return
	}

	res := &response.AddGroupUser{
		ID:           u.ID,
		Name:         u.Name,
		Username:     u.Username,
		Email:        u.Email,
		ThumbnailURL: u.ThumbnailURL,
		GroupIDs:     u.GroupIDs,
		CreatedAt:    u.CreatedAt,
		UpdatedAt:    u.UpdatedAt,
	}

	ctx.JSON(http.StatusOK, res)
}

func (uh *apiV1UserHandler) RemoveGroup(ctx *gin.Context) {
	userID := ctx.Params.ByName("userID")
	groupID := ctx.Params.ByName("groupID")

	c := middleware.GinContextToContext(ctx)
	u, err := uh.userApplication.RemoveGroupID(c, userID, groupID)
	if err != nil {
		handler.ErrorHandling(ctx, err)
		return
	}

	res := &response.RemoveGroupUser{
		ID:           u.ID,
		Name:         u.Name,
		Username:     u.Username,
		Email:        u.Email,
		ThumbnailURL: u.ThumbnailURL,
		GroupIDs:     u.GroupIDs,
		CreatedAt:    u.CreatedAt,
		UpdatedAt:    u.UpdatedAt,
	}

	ctx.JSON(http.StatusOK, res)
}
