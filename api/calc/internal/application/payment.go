package application

import (
	"context"
	"encoding/base64"
	"strings"

	"github.com/calmato/presto-pay/api/calc/internal/application/request"
	"github.com/calmato/presto-pay/api/calc/internal/application/validation"
	"github.com/calmato/presto-pay/api/calc/internal/domain"
	"github.com/calmato/presto-pay/api/calc/internal/domain/payment"
	"github.com/calmato/presto-pay/api/calc/internal/domain/user"
	"golang.org/x/xerrors"
)

// PaymentApplication - PaymentApplicationインターフェース
type PaymentApplication interface {
	Create(ctx context.Context, req *request.CreatePayment, groupID string) (*payment.Payment, error)
}

type paymentApplication struct {
	paymentRequestValidation validation.PaymentRequestValidation
	userService              user.UserService
	paymentService           payment.PaymentService
}

// NewPaymentApplication - PaymentApplicationの生成
func NewPaymentApplication(
	prv validation.PaymentRequestValidation, us user.UserService, ps payment.PaymentService,
) PaymentApplication {
	return &paymentApplication{
		paymentRequestValidation: prv,
		userService:              us,
		paymentService:           ps,
	}
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

	payers := make([]*payment.Payer, len(req.Payers))
	for i, payer := range req.Payers {
		payers[i] = &payment.Payer{
			ID:     payer.ID,
			Amount: payer.Amount,
		}
	}

	p := &payment.Payment{
		Name:      req.Name,
		Currency:  req.Currency,
		Total:     req.Total,
		Tags:      req.Tags,
		Comment:   req.Comment,
		ImageURLs: imageURLs,
		Payers:    payers,
	}

	if _, err = pa.paymentService.Create(ctx, p, groupID); err != nil {
		return nil, err
	}

	return p, nil
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
