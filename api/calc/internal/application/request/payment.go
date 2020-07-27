package request

import "time"

// PayerInCreatePayment - 支払い情報作成用 支払い者情報
type PayerInCreatePayment struct {
	ID     string `json:"id"`
	Amount int    `json:"amount"` // TODO: 合計が0になるように
}

// CreatePayment - 支払い情報作成APIのリクエスト
type CreatePayment struct {
	Name     string                  `json:"name" validate:"required,max=64"`
	Currency string                  `json:"currency"`
	Total    uint                    `json:"total" validate:"required,gte=0,lt=65536"`
	Payers   []*PayerInCreatePayment `json:"payers" validate:"min=2"`
	Tags     []string                `json:"tags" validate:"min=0,dive,max=32"`
	Comment  string                  `json:"comment" validate:"max=256"`
	Images   []string                `json:"images"`
	PaidAt   time.Time               `json:"paidAt" validate:"required"`
}
