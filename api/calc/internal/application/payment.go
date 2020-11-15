package application

import (
	"context"
	"encoding/base64"
	"fmt"
	"strings"

	"github.com/calmato/presto-pay/api/calc/internal/application/request"
	"github.com/calmato/presto-pay/api/calc/internal/application/validation"
	"github.com/calmato/presto-pay/api/calc/internal/domain"
	"github.com/calmato/presto-pay/api/calc/internal/domain/exchange"
	"github.com/calmato/presto-pay/api/calc/internal/domain/payment"
	"github.com/calmato/presto-pay/api/calc/internal/domain/user"
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
	UpdateStatusAll(ctx context.Context, groupID string) ([]*payment.Payment, error)
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

	fmt.Println(ers)

	// ユーザー毎の支払額合計作成
	payers := map[string]*payment.Payer{}
	for _, p := range ps {
		// 支払い情報に登録されている通貨情報を取得
		currentRate := ers.Rates[p.Currency]
		if currentRate == 0 {
			currentRate = ers.Rates[ers.Base]
		}

		for _, payer := range p.Payers {
			if payer == nil {
				continue
			}

			// 初期データを投入
			if payers[payer.ID] == nil {
				payers[payer.ID] = &payment.Payer{
					ID:     payer.ID,
					Name:   payer.Name,
					Amount: 0,
					IsPaid: true,
				}
			}

			// 1つでも未支払いのものがあれば、IsPaidをfalseに変更
			if !payer.IsPaid {
				payers[payer.ID].IsPaid = false
				payer.Amount = payer.Amount * -1
			}

			// 為替レートの反映
			payers[payer.ID].Amount += payer.Amount * ers.Rates[currency] / currentRate
		}
	}

	return ps, payers, currency, err
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

	total := 0.0
	payers := make([]*payment.Payer, 0)
	for _, payer := range req.PositivePayers {
		p := &payment.Payer{
			ID:     payer.ID,
			Amount: payer.Amount,
			IsPaid: true,
		}

		total += payer.Amount
		payers = append(payers, p)
	}

	for _, payer := range req.NegativePayers {
		p := &payment.Payer{
			ID:     payer.ID,
			Amount: payer.Amount,
			IsPaid: false,
		}

		// PositivePayersとNegativePayers両方に同じユーザーいれてる場合あるらしいからそれの対策
		// -> あればpにマージして配列の要素は削除
		for i, v := range payers {
			if v.ID != p.ID {
				continue
			}

			amount := payers[i].Amount - p.Amount
			if amount > 0 {
				p.Amount = amount
				p.IsPaid = true
			} else {
				p.Amount = amount * -1
				p.IsPaid = false
			}

			payers = append(payers[:i], payers[i+1:]...)
			break
		}

		payers = append(payers, p)
	}

	p := &payment.Payment{
		Name:      req.Name,
		Currency:  req.Currency,
		Total:     total,
		Tags:      req.Tags,
		Comment:   req.Comment,
		ImageURLs: imageURLs,
		Payers:    payers,
		PaidAt:    req.PaidAt,
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

	total := 0.0
	payers := make([]*payment.Payer, 0)
	for _, payer := range req.PositivePayers {
		p := &payment.Payer{
			ID:     payer.ID,
			Amount: payer.Amount,
			IsPaid: true,
		}

		total += p.Amount
		payers = append(payers, p)
	}

	for _, payer := range req.NegativePayers {
		p := &payment.Payer{
			ID:     payer.ID,
			Amount: payer.Amount,
			IsPaid: false,
		}

		// PositivePayersとNegativePayers両方に同じユーザーいれてる場合あるらしいからそれの対策
		// -> あればpにマージして配列の要素は削除
		for i, v := range payers {
			if v.ID != p.ID {
				continue
			}

			amount := payers[i].Amount - p.Amount
			if amount > 0 {
				p.Amount = amount
				p.IsPaid = true
			} else {
				p.Amount = amount * -1
				p.IsPaid = false
			}

			payers = append(payers[:i], payers[i+1:]...)
			break
		}

		payers = append(payers, p)
	}

	p.Name = req.Name
	p.Currency = req.Currency
	p.Total = total
	p.Payers = payers
	p.IsCompleted = req.IsCompleted
	p.Tags = req.Tags
	p.Comment = req.Comment
	p.ImageURLs = append(p.ImageURLs, imageURLs...)
	p.PaidAt = req.PaidAt

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

	payer := &payment.Payer{
		ID:     payerID,
		IsPaid: req.IsPaid,
	}

	p, err := pa.paymentService.UpdatePayer(ctx, groupID, paymentID, payer)
	if err != nil {
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

	p.IsCompleted = !p.IsCompleted
	for i := 0; i < len(p.Payers); i++ {
		p.Payers[i].IsPaid = p.IsCompleted
	}

	if _, err := pa.paymentService.Update(ctx, p, groupID); err != nil {
		return nil, err
	}

	return p, nil
}

// UpdateStatusAll - グループ内の支払い情報全ての支払い完了フラグを完了に
func (pa *paymentApplication) UpdateStatusAll(ctx context.Context, groupID string) ([]*payment.Payment, error) {
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

	ps, err := pa.paymentService.IndexByIsCompleted(ctx, groupID, true) // TODO: refactor
	if err != nil {
		return nil, err
	}

	for i, p := range ps {
		p.IsCompleted = !p.IsCompleted
		for i := 0; i < len(p.Payers); i++ {
			p.Payers[i].IsPaid = p.IsCompleted
		}

		if _, err := pa.paymentService.Update(ctx, p, groupID); err != nil {
			return nil, err
		}

		ps[i] = p
	}

	return ps, nil
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
