package application

import (
	"context"
	"encoding/base64"
	"math"
	"strings"

	"github.com/calmato/presto-pay/api/calc/internal/application/request"
	"github.com/calmato/presto-pay/api/calc/internal/application/validation"
	"github.com/calmato/presto-pay/api/calc/internal/domain"
	"github.com/calmato/presto-pay/api/calc/internal/domain/exchange"
	"github.com/calmato/presto-pay/api/calc/internal/domain/payment"
	"github.com/calmato/presto-pay/api/calc/internal/domain/user"
	"github.com/calmato/presto-pay/api/calc/lib/common"
	"golang.org/x/xerrors"
)

// PaymentApplication - PaymentApplicationインターフェース
type PaymentApplication interface {
	Index(
		ctx context.Context, groupID string, startAt string, currency string,
	) ([]*payment.Payment, map[string]*payment.Payer, string, error)
	Create(ctx context.Context, req *request.CreatePayment, groupID string) (*payment.Payment, error)
	Update(ctx context.Context, req *request.UpdatePayment, groupID string, paymentID string) (*payment.Payment, error)
	UpdatePayer(
		ctx context.Context, req *request.UpdatePayerInPayment, groupID string, paymentID string, payerID string,
	) (*payment.Payment, error)
	UpdateStatus(ctx context.Context, groupID string, paymentID string) (*payment.Payment, error)
	UpdateStatusAll(ctx context.Context, groupID string) ([]*payment.Payment, map[string]*payment.Payer, string, error)
	Destroy(ctx context.Context, groupID string, paymentID string) error
}

type paymentApplication struct {
	paymentRequestValidation validation.PaymentRequestValidation
	userService              user.UserService
	paymentService           payment.PaymentService
	exchangeService          exchange.ExchangeService
}

// NewPaymentApplication - PaymentApplicationの生成
func NewPaymentApplication(
	prv validation.PaymentRequestValidation, us user.UserService,
	ps payment.PaymentService, es exchange.ExchangeService,
) PaymentApplication {
	return &paymentApplication{
		paymentRequestValidation: prv,
		userService:              us,
		paymentService:           ps,
		exchangeService:          es,
	}
}

// TODO: startAtの引数追加
func (pa *paymentApplication) Index(
	ctx context.Context, groupID string, startAt string, currency string,
) ([]*payment.Payment, map[string]*payment.Payer, string, error) {
	u, err := pa.userService.Authentication(ctx)
	if err != nil {
		return nil, nil, "", domain.Unauthorized.New(err)
	}

	contain, err := pa.userService.ContainsGroupID(ctx, u, groupID)
	if err != nil {
		return nil, nil, "", err
	}

	if !contain {
		err := xerrors.New("Failed to Service")
		return nil, nil, "", domain.Forbidden.New(err)
	}

	ps, err := pa.paymentService.Index(ctx, groupID, startAt)
	if err != nil {
		return nil, nil, "", err
	}

	// 為替レート一覧のmapを作成
	ers, err := pa.exchangeService.Index(ctx)
	if err != nil {
		return nil, nil, "", err
	}

	currency = strings.ToLower(currency)
	if ers.Rates[currency] == 0 {
		currency = ers.Base
	}

	// ユーザー毎の支払額合計作成
	pys := map[string]*payment.Payer{}
	for _, p := range ps {
		// 支払い情報に登録されている通貨情報を取得
		currentRate := ers.Rates[p.Currency]
		if currentRate == 0 {
			currentRate = ers.Rates[ers.Base]
		}

		for _, py := range p.Payers {
			if py == nil {
				continue
			}

			// 初期データを投入
			if pys[py.ID] == nil {
				pys[py.ID] = &payment.Payer{
					ID:     py.ID,
					Name:   py.Name,
					Amount: 0,
					IsPaid: true,
				}
			}

			// 1つでも未支払いのものがあれば、IsPaidをfalseに変更
			if !py.IsPaid {
				pys[py.ID].IsPaid = false
				py.Amount *= -1
			}

			// 為替レートの反映
			pys[py.ID].Amount += py.Amount * ers.Rates[currency] / currentRate
		}
	}

	return ps, pys, currency, err
}

func (pa *paymentApplication) Create(
	ctx context.Context, req *request.CreatePayment, groupID string,
) (*payment.Payment, error) {
	u, err := pa.userService.Authentication(ctx)
	if err != nil {
		return nil, domain.Unauthorized.New(err)
	}

	if ves := pa.paymentRequestValidation.CreatePayment(req); len(ves) > 0 {
		err := xerrors.New("Failed to RequestValidation")
		return nil, domain.InvalidRequestValidation.New(err, ves...)
	}

	contain, err := pa.userService.ContainsGroupID(ctx, u, groupID)
	if err != nil {
		return nil, err
	}

	if !contain {
		err := xerrors.New("Failed to Service")
		return nil, domain.Forbidden.New(err)
	}

	imageURLs := make([]string, len(req.Images))
	for i, image := range req.Images {
		imageURL, err := getImageURL(ctx, pa, image)
		if err != nil {
			return nil, err
		}

		imageURLs[i] = imageURL
	}

	total, pys := collectPayers(req.PositivePayers, req.NegativePayers)

	p := &payment.Payment{
		Name:        req.Name,
		Currency:    req.Currency,
		Total:       total,
		Tags:        req.Tags,
		Comment:     req.Comment,
		ImageURLs:   imageURLs,
		Payers:      pys,
		IsCompleted: false,
		PaidAt:      common.StringToTime(req.PaidAt),
	}

	if _, err = pa.paymentService.Create(ctx, p, groupID); err != nil {
		return nil, err
	}

	return p, nil
}

func (pa *paymentApplication) Update(
	ctx context.Context, req *request.UpdatePayment, groupID string, paymentID string,
) (*payment.Payment, error) {
	u, err := pa.userService.Authentication(ctx)
	if err != nil {
		return nil, domain.Unauthorized.New(err)
	}

	if ves := pa.paymentRequestValidation.UpdatePayment(req); len(ves) > 0 {
		err := xerrors.New("Failed to RequestValidation")
		return nil, domain.InvalidRequestValidation.New(err, ves...)
	}

	contain, err := pa.userService.ContainsGroupID(ctx, u, groupID)
	if err != nil {
		return nil, err
	}

	if !contain {
		err := xerrors.New("Failed to Application")
		return nil, domain.Forbidden.New(err)
	}

	p, err := pa.paymentService.Show(ctx, groupID, paymentID)
	if err != nil {
		return nil, err
	}

	imageURLs := make([]string, len(req.Images))
	for i, image := range req.Images {
		imageURL, err := getImageURL(ctx, pa, image)
		if err != nil {
			return nil, err
		}

		imageURLs[i] = imageURL
	}

	total, pys := collectPayers(req.PositivePayers, req.NegativePayers)

	p.Name = req.Name
	p.Currency = req.Currency
	p.Total = total
	p.Payers = pys
	p.IsCompleted = len(req.NegativePayers) == 0
	p.Tags = req.Tags
	p.Comment = req.Comment
	p.ImageURLs = append(p.ImageURLs, imageURLs...)
	p.PaidAt = common.StringToTime(req.PaidAt)

	if _, err := pa.paymentService.Update(ctx, p, groupID); err != nil {
		return nil, err
	}

	return p, nil
}

func (pa *paymentApplication) UpdatePayer(
	ctx context.Context, req *request.UpdatePayerInPayment, groupID string, paymentID string, payerID string,
) (*payment.Payment, error) {
	u, err := pa.userService.Authentication(ctx)
	if err != nil {
		return nil, domain.Unauthorized.New(err)
	}

	if ves := pa.paymentRequestValidation.UpdatePayerInPayment(req); len(ves) > 0 {
		err := xerrors.New("Failed to RequestValidation")
		return nil, domain.InvalidRequestValidation.New(err, ves...)
	}

	contain, err := pa.userService.ContainsGroupID(ctx, u, groupID)
	if err != nil {
		return nil, err
	}

	if !contain {
		err := xerrors.New("Failed to Application")
		return nil, domain.Forbidden.New(err)
	}

	p, err := pa.paymentService.Show(ctx, groupID, paymentID)
	if err != nil {
		return nil, err
	}

	if _, err = pa.paymentService.UpdatePayer(ctx, p, groupID, payerID, req.IsPaid); err != nil {
		return nil, err
	}

	return p, nil
}

// UpdateStatus - 支払い完了フラグの更新
func (pa *paymentApplication) UpdateStatus(
	ctx context.Context, groupID string, paymentID string,
) (*payment.Payment, error) {
	u, err := pa.userService.Authentication(ctx)
	if err != nil {
		return nil, domain.Unauthorized.New(err)
	}

	contain, err := pa.userService.ContainsGroupID(ctx, u, groupID)
	if err != nil {
		return nil, err
	}

	if !contain {
		err := xerrors.New("Failed to Application")
		return nil, domain.Forbidden.New(err)
	}

	p, err := pa.paymentService.Show(ctx, groupID, paymentID)
	if err != nil {
		return nil, err
	}

	p.IsCompleted = true

	if _, err := pa.paymentService.Update(ctx, p, groupID); err != nil {
		return nil, err
	}

	return p, nil
}

// UpdateStatusAll - グループ内の支払い情報全ての支払い完了フラグを完了に
func (pa *paymentApplication) UpdateStatusAll(
	ctx context.Context, groupID string,
) ([]*payment.Payment, map[string]*payment.Payer, string, error) {
	u, err := pa.userService.Authentication(ctx)
	if err != nil {
		return nil, nil, "", domain.Unauthorized.New(err)
	}

	contain, err := pa.userService.ContainsGroupID(ctx, u, groupID)
	if err != nil {
		return nil, nil, "", err
	}

	if !contain {
		err := xerrors.New("Failed to Application")
		return nil, nil, "", domain.Forbidden.New(err)
	}

	ps, err := pa.paymentService.IndexByIsCompleted(ctx, groupID, false) // TODO: refactor
	if err != nil {
		return nil, nil, "", err
	}

	for _, p := range ps {
		p.IsCompleted = true

		if _, err := pa.paymentService.Update(ctx, p, groupID); err != nil {
			return nil, nil, "", err
		}
	}

	// 為替レート一覧のmapを作成
	ers, err := pa.exchangeService.Index(ctx)
	if err != nil {
		return nil, nil, "", err
	}

	currency := ers.Base

	// ユーザー毎の支払額合計作成
	pys := map[string]*payment.Payer{}
	for _, p := range ps {
		// 支払い情報に登録されている通貨情報を取得
		currentRate := ers.Rates[p.Currency]
		if currentRate == 0 {
			currentRate = ers.Rates[ers.Base]
		}

		for _, py := range p.Payers {
			if py == nil {
				continue
			}

			// 初期データを投入
			if pys[py.ID] == nil {
				pys[py.ID] = &payment.Payer{
					ID:     py.ID,
					Name:   py.Name,
					Amount: 0,
					IsPaid: true,
				}
			}

			// 1つでも未支払いのものがあれば、IsPaidをfalseに変更
			if !py.IsPaid {
				pys[py.ID].IsPaid = false
				py.Amount *= -1
			}

			// 為替レートの反映
			pys[py.ID].Amount += py.Amount * ers.Rates[currency] / currentRate
		}
	}

	return ps, pys, currency, nil
}

func (pa *paymentApplication) Destroy(ctx context.Context, groupID string, paymentID string) error {
	u, err := pa.userService.Authentication(ctx)
	if err != nil {
		return domain.Unauthorized.New(err)
	}

	contain, err := pa.userService.ContainsGroupID(ctx, u, groupID)
	if err != nil {
		return err
	}

	if !contain {
		err := xerrors.New("Failed to Application")
		return domain.Forbidden.New(err)
	}

	_, err = pa.paymentService.Show(ctx, groupID, paymentID)
	if err != nil {
		return err
	}

	if err = pa.paymentService.Destroy(ctx, groupID, paymentID); err != nil {
		return err
	}

	return nil
}

func getImageURL(ctx context.Context, pa *paymentApplication, image string) (string, error) {
	imageURL := ""

	if image != "" {
		// data:image/png;base64,iVBORw0KGgoAAAA... みたいなのうちの
		// `data:image/png;base64,` の部分を無くした []byte を取得
		b64data := image[strings.IndexByte(image, ',')+1:]

		data, err := base64.StdEncoding.DecodeString(b64data)
		if err != nil {
			ve := &domain.ValidationError{
				Field:   "images",
				Message: domain.UnableConvertBase64Massage,
			}

			return "", domain.UnableConvertBase64.New(err, ve)
		}

		imageURL, err = pa.paymentService.UploadImage(ctx, data)
		if err != nil {
			err = xerrors.Errorf("Failed to Application: %w", err)
			return "", domain.ErrorInStorage.New(err)
		}
	}

	return imageURL, nil
}

func collectPayers(pps []*request.PayerInPayment, nps []*request.PayerInPayment) (float64, []*payment.Payer) {
	total := 0.0

	// 支払済情報
	ps := make([]*payment.Payer, 0)
	for _, pp := range pps {
		p := &payment.Payer{
			ID:     pp.ID,
			Amount: math.Abs(pp.Amount),
			IsPaid: true,
		}

		total += p.Amount
		ps = append(ps, p)
	}

	// 未支払い情報
	for _, np := range nps {
		p := &payment.Payer{
			ID:     np.ID,
			Amount: math.Abs(np.Amount),
			IsPaid: false,
		}

		// PositivePayersとNegativePayers両方に同じユーザーいれてる場合あるらしいからそれの対策
		if i, ok := containPayers(p, ps); ok {
			amount := ps[i].Amount - p.Amount
			if amount < 0 {
				p.Amount = amount * -1
				p.IsPaid = false
			} else {
				p.Amount = amount
				p.IsPaid = true
			}

			ps = append(ps[:i], ps[i+1:]...)
		}

		ps = append(ps, p)
	}

	return total, ps
}

func containPayers(np *payment.Payer, ps []*payment.Payer) (int, bool) {
	for i, p := range ps {
		if np.ID == p.ID {
			return i, true
		}
	}

	return 0, false
}
