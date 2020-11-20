package v1

import (
	"math"
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
	Update(ctx *gin.Context)
	UpdateStatus(ctx *gin.Context)
	UpdateStatusAll(ctx *gin.Context)
	UpdatePayer(ctx *gin.Context)
	Destroy(ctx *gin.Context)
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
	currency := ctx.DefaultQuery("currency", "")

	c := middleware.GinContextToContext(ctx)
	payments, payers, currency, err := ph.paymentApplication.Index(c, groupID, startAt, currency)
	if err != nil {
		handler.ErrorHandling(ctx, err)
		return
	}

	paymentsResponse := make([]*response.PaymentInIndexPayments, len(payments))
	for i, payment := range payments {
		payersResponse := make([]*response.PayerInIndexPayments, len(payment.Payers))
		positivePayersResponse := make([]*response.PayerInIndexPayments, 0)
		negativePayersResponse := make([]*response.PayerInIndexPayments, 0)
		for j, payer := range payment.Payers {
			pr := &response.PayerInIndexPayments{
				ID:     payer.ID,
				Name:   payer.Name,
				Amount: payer.Amount,
			}

			prAbs := &response.PayerInIndexPayments{
				ID:     payer.ID,
				Name:   payer.Name,
				Amount: math.Abs(payer.Amount),
			}

			payersResponse[j] = pr

			if payer.IsPaid {
				positivePayersResponse = append(positivePayersResponse, prAbs)
			} else {
				negativePayersResponse = append(negativePayersResponse, prAbs)
			}
		}

		paymentsResponse[i] = &response.PaymentInIndexPayments{
			ID:             payment.ID,
			GroupID:        payment.GroupID,
			Name:           payment.Name,
			Currency:       payment.Currency,
			Total:          payment.Total,
			Payers:         payersResponse,
			PostivePayers:  positivePayersResponse,
			NegativePayers: negativePayersResponse,
			Tags:           payment.Tags,
			Comment:        payment.Comment,
			ImageURLs:      payment.ImageURLs,
			PaidAt:         payment.PaidAt,
			IsCompleted:    payment.IsCompleted,
			CreatedAt:      payment.CreatedAt,
			UpdatedAt:      payment.UpdatedAt,
		}
	}

	usersResponse := map[string]*response.UserInIndexPayments{}
	for id, payer := range payers {
		usersResponse[id] = &response.UserInIndexPayments{
			ID:     payer.ID,
			Name:   payer.Name,
			Amount: payer.Amount,
		}
	}

	res := &response.IndexPayments{
		Payments: paymentsResponse,
		Users:    usersResponse,
		Currency: currency,
	}

	ctx.JSON(http.StatusOK, res)
}

func (ph *apiV1PaymentHandler) Create(ctx *gin.Context) {
	groupID := ctx.Params.ByName("groupID")

	req := &request.CreatePayment{}
	if err := ctx.BindJSON(req); err != nil {
		handler.ErrorHandling(ctx, domain.UnableParseJSON.New(err))
		return
	}

	c := middleware.GinContextToContext(ctx)
	p, err := ph.paymentApplication.Create(c, req, groupID)
	if err != nil {
		handler.ErrorHandling(ctx, err)
		return
	}

	payers := make([]*response.PayerInCreatePayment, len(p.Payers))
	positivePayers := make([]*response.PayerInCreatePayment, 0)
	negativePayers := make([]*response.PayerInCreatePayment, 0)
	for i, payer := range p.Payers {
		pr := &response.PayerInCreatePayment{
			ID:     payer.ID,
			Amount: payer.Amount,
		}

		payers[i] = pr

		if payer.IsPaid {
			positivePayers = append(positivePayers, pr)
		} else {
			negativePayers = append(negativePayers, pr)
		}
	}

	res := &response.CreatePayment{
		ID:             p.ID,
		GroupID:        p.GroupID,
		Name:           p.Name,
		Currency:       p.Currency,
		Total:          p.Total,
		Payers:         payers,
		PostivePayers:  positivePayers,
		NegativePayers: negativePayers,
		Tags:           p.Tags,
		Comment:        p.Comment,
		ImageURLs:      p.ImageURLs,
		PaidAt:         p.PaidAt,
		IsCompleted:    p.IsCompleted,
		CreatedAt:      p.CreatedAt,
		UpdatedAt:      p.UpdatedAt,
	}

	ctx.JSON(http.StatusOK, res)
}

func (ph *apiV1PaymentHandler) Update(ctx *gin.Context) {
	groupID := ctx.Params.ByName("groupID")
	paymentID := ctx.Params.ByName("paymentID")

	req := &request.UpdatePayment{}
	if err := ctx.BindJSON(req); err != nil {
		handler.ErrorHandling(ctx, domain.UnableParseJSON.New(err))
		return
	}

	c := middleware.GinContextToContext(ctx)
	p, err := ph.paymentApplication.Update(c, req, groupID, paymentID)
	if err != nil {
		handler.ErrorHandling(ctx, err)
		return
	}

	payers := make([]*response.PayerInUpdatePayment, len(p.Payers))
	positivePayers := make([]*response.PayerInUpdatePayment, 0)
	negativePayers := make([]*response.PayerInUpdatePayment, 0)
	for i, payer := range p.Payers {
		pr := &response.PayerInUpdatePayment{
			ID:     payer.ID,
			Amount: payer.Amount,
		}

		payers[i] = pr

		if payer.IsPaid {
			positivePayers = append(positivePayers, pr)
		} else {
			negativePayers = append(negativePayers, pr)
		}
	}

	res := &response.UpdatePayment{
		ID:             p.ID,
		GroupID:        p.GroupID,
		Name:           p.Name,
		Currency:       p.Currency,
		Total:          p.Total,
		Payers:         payers,
		PostivePayers:  positivePayers,
		NegativePayers: negativePayers,
		Tags:           p.Tags,
		Comment:        p.Comment,
		ImageURLs:      p.ImageURLs,
		PaidAt:         p.PaidAt,
		IsCompleted:    p.IsCompleted,
		CreatedAt:      p.CreatedAt,
		UpdatedAt:      p.UpdatedAt,
	}

	ctx.JSON(http.StatusOK, res)
}

func (ph *apiV1PaymentHandler) UpdateStatus(ctx *gin.Context) {
	groupID := ctx.Params.ByName("groupID")
	paymentID := ctx.Params.ByName("paymentID")

	c := middleware.GinContextToContext(ctx)
	p, err := ph.paymentApplication.UpdateStatus(c, groupID, paymentID)
	if err != nil {
		handler.ErrorHandling(ctx, err)
		return
	}

	payers := make([]*response.PayerInUpdatePayment, len(p.Payers))
	positivePayers := make([]*response.PayerInUpdatePayment, 0)
	negativePayers := make([]*response.PayerInUpdatePayment, 0)
	for i, payer := range p.Payers {
		pr := &response.PayerInUpdatePayment{
			ID:     payer.ID,
			Amount: payer.Amount,
		}

		payers[i] = pr

		if payer.IsPaid {
			positivePayers = append(positivePayers, pr)
		} else {
			negativePayers = append(negativePayers, pr)
		}
	}

	res := &response.UpdatePayment{
		ID:             p.ID,
		GroupID:        p.GroupID,
		Name:           p.Name,
		Currency:       p.Currency,
		Total:          p.Total,
		Payers:         payers,
		PostivePayers:  positivePayers,
		NegativePayers: negativePayers,
		Tags:           p.Tags,
		Comment:        p.Comment,
		ImageURLs:      p.ImageURLs,
		PaidAt:         p.PaidAt,
		IsCompleted:    p.IsCompleted,
		CreatedAt:      p.CreatedAt,
		UpdatedAt:      p.UpdatedAt,
	}

	ctx.JSON(http.StatusOK, res)
}

func (ph *apiV1PaymentHandler) UpdateStatusAll(ctx *gin.Context) {
	groupID := ctx.Params.ByName("groupID")

	c := middleware.GinContextToContext(ctx)
	ps, err := ph.paymentApplication.UpdateStatusAll(c, groupID)
	if err != nil {
		handler.ErrorHandling(ctx, err)
		return
	}

	prs := make([]*response.UpdatePayment, len(ps))
	for i, p := range ps {
		payers := make([]*response.PayerInUpdatePayment, len(p.Payers))
		positivePayers := make([]*response.PayerInUpdatePayment, 0)
		negativePayers := make([]*response.PayerInUpdatePayment, 0)
		for i, payer := range p.Payers {
			pr := &response.PayerInUpdatePayment{
				ID:     payer.ID,
				Amount: payer.Amount,
			}

			payers[i] = pr

			if payer.IsPaid {
				positivePayers = append(positivePayers, pr)
			} else {
				negativePayers = append(negativePayers, pr)
			}
		}

		pr := &response.UpdatePayment{
			ID:             p.ID,
			GroupID:        p.GroupID,
			Name:           p.Name,
			Currency:       p.Currency,
			Total:          p.Total,
			Payers:         payers,
			PostivePayers:  positivePayers,
			NegativePayers: negativePayers,
			Tags:           p.Tags,
			Comment:        p.Comment,
			ImageURLs:      p.ImageURLs,
			PaidAt:         p.PaidAt,
			IsCompleted:    p.IsCompleted,
			CreatedAt:      p.CreatedAt,
			UpdatedAt:      p.UpdatedAt,
		}

		prs[i] = pr
	}

	res := &response.UpdatePayments{
		Payments: prs,
	}

	ctx.JSON(http.StatusOK, res)
}

func (ph *apiV1PaymentHandler) UpdatePayer(ctx *gin.Context) {
	groupID := ctx.Params.ByName("groupID")
	paymentID := ctx.Params.ByName("paymentID")
	payerID := ctx.Params.ByName("payerID")

	req := &request.UpdatePayerInPayment{}
	if err := ctx.BindJSON(req); err != nil {
		handler.ErrorHandling(ctx, domain.UnableParseJSON.New(err))
		return
	}

	c := middleware.GinContextToContext(ctx)
	p, err := ph.paymentApplication.UpdatePayer(c, req, groupID, paymentID, payerID)
	if err != nil {
		handler.ErrorHandling(ctx, err)
		return
	}

	payers := make([]*response.PayerInUpdatePayment, len(p.Payers))
	positivePayers := make([]*response.PayerInUpdatePayment, 0)
	negativePayers := make([]*response.PayerInUpdatePayment, 0)
	for i, payer := range p.Payers {
		pr := &response.PayerInUpdatePayment{
			ID:     payer.ID,
			Amount: payer.Amount,
		}

		payers[i] = pr

		if payer.IsPaid {
			positivePayers = append(positivePayers, pr)
		} else {
			negativePayers = append(negativePayers, pr)
		}
	}

	res := &response.UpdatePayment{
		ID:             p.ID,
		GroupID:        p.GroupID,
		Name:           p.Name,
		Currency:       p.Currency,
		Total:          p.Total,
		Payers:         payers,
		PostivePayers:  positivePayers,
		NegativePayers: negativePayers,
		Tags:           p.Tags,
		Comment:        p.Comment,
		ImageURLs:      p.ImageURLs,
		PaidAt:         p.PaidAt,
		IsCompleted:    p.IsCompleted,
		CreatedAt:      p.CreatedAt,
		UpdatedAt:      p.UpdatedAt,
	}

	ctx.JSON(http.StatusOK, res)
}

func (ph *apiV1PaymentHandler) Destroy(ctx *gin.Context) {
	groupID := ctx.Params.ByName("groupID")
	paymentID := ctx.Params.ByName("paymentID")

	c := middleware.GinContextToContext(ctx)
	if err := ph.paymentApplication.Destroy(c, groupID, paymentID); err != nil {
		handler.ErrorHandling(ctx, err)
		return
	}

	ctx.JSON(http.StatusOK, gin.H{})
}
