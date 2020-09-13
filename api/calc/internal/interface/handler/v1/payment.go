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

// APIV1PaymentHandler - Paymentハンドラのインターフェース
type APIV1PaymentHandler interface {
	Index(ctx *gin.Context)
	Create(ctx *gin.Context)
}

type apiV1PaymentHandler struct {
	paymentApplication application.PaymentApplication
}

// NewAPIV1PaymentHandler - APIV1PaymentHandlerの生成
func NewAPIV1PaymentHandler(pa application.PaymentApplication) APIV1PaymentHandler {
	return &apiV1PaymentHandler{
		paymentApplication: pa,
	}
}

func (ph *apiV1PaymentHandler) Index(ctx *gin.Context) {
	groupID := ctx.Params.ByName("groupID")
	startAt := ctx.DefaultQuery("after", "")

	c := middleware.GinContextToContext(ctx)
	ps, err := ph.paymentApplication.Index(c, groupID, startAt)
	if err != nil {
		handler.ErrorHandling(ctx, err)
		return
	}

	payments := make([]*response.PaymentInIndexPayments, len(ps))
	for i, p := range ps {
		payers := make([]*response.PayerInIndexPayments, len(p.Payers))
		for j, payer := range p.Payers {
			payers[j] = &response.PayerInIndexPayments{
				ID:     payer.ID,
				Name:   payer.Name,
				Amount: payer.Amount,
				IsPaid: payer.IsPaid,
			}
		}

		payments[i] = &response.PaymentInIndexPayments{
			ID:          p.ID,
			Name:        p.Name,
			Currency:    p.Currency,
			Total:       p.Total,
			Payers:      payers,
			Tags:        p.Tags,
			Comment:     p.Comment,
			ImageURLs:   p.ImageURLs,
			PaidAt:      p.PaidAt,
			IsCompleted: p.IsCompleted,
			CreatedAt:   p.CreatedAt,
			UpdatedAt:   p.UpdatedAt,
		}
	}

	res := &response.IndexPayments{
		Payments: payments,
	}

	ctx.JSON(http.StatusOK, res)
}

func (ph *apiV1PaymentHandler) Create(ctx *gin.Context) {
	groupID := ctx.Params.ByName("groupID")

	req := &request.CreatePayment{}
	if err := ctx.BindJSON(req); err != nil {
		handler.ErrorHandling(ctx, domain.Unauthorized.New(err))
		return
	}

	c := middleware.GinContextToContext(ctx)
	p, err := ph.paymentApplication.Create(c, req, groupID)
	if err != nil {
		handler.ErrorHandling(ctx, err)
		return
	}

	payers := make([]*response.PayerInCreatePayment, len(p.Payers))
	for i, payer := range p.Payers {
		payers[i] = &response.PayerInCreatePayment{
			ID:     payer.ID,
			Amount: payer.Amount,
			IsPaid: payer.IsPaid,
		}
	}

	res := &response.CreatePayment{
		ID:          p.ID,
		Name:        p.Name,
		Currency:    p.Currency,
		Total:       p.Total,
		Payers:      payers,
		Tags:        p.Tags,
		Comment:     p.Comment,
		ImageURLs:   p.ImageURLs,
		PaidAt:      p.PaidAt,
		IsCompleted: p.IsCompleted,
		CreatedAt:   p.CreatedAt,
		UpdatedAt:   p.UpdatedAt,
	}

	ctx.JSON(http.StatusOK, res)
}
