package request

import "time"

// PayerInCreatePayment - 支払い情報作成用 支払い者情報
type PayerInCreatePayment struct {
	ID     string  `json:"id"`
	Amount float64 `json:"amount"` // TODO: 合計が0になるように
}

// CreatePayment - 支払い情報作成APIのリクエスト
type CreatePayment struct {
	Name           string                  `json:"name" validate:"required,max=64"`
	Currency       string                  `json:"currency"`
	Total          float64                 `json:"total" validate:"required,gte=0,lt=1048576"`
	PositivePayers []*PayerInCreatePayment `json:"positivePayers" validate:"min=0"`
	NegativePayers []*PayerInCreatePayment `json:"negativePayers" validate:"min=0"`
	Tags           []string                `json:"tags" validate:"min=0,dive,max=32"`
	Comment        string                  `json:"comment" validate:"max=256"`
	Images         []string                `json:"images"`
	PaidAt         time.Time               `json:"paidAt" validate:"required"`
}

// PayerInUpdatePayment - 支払い情報編集用 支払い者情報
type PayerInUpdatePayment struct {
	ID     string  `json:"id"`
	Amount float64 `json:"amount"` // TODO: 合計が0になるように
	IsPaid bool    `json:"isPaid" validate:"required"`
}

// UpdatePayment - 支払い情報作成APIのリクエスト
type UpdatePayment struct {
	Name           string                  `json:"name" validate:"required,max=64"`
	Currency       string                  `json:"currency"`
	Total          float64                 `json:"total" validate:"required,gte=0,lt=1048576"`
	PositivePayers []*PayerInCreatePayment `json:"positivePayers" validate:"min=0"`
	NegativePayers []*PayerInCreatePayment `json:"negativePayers" validate:"min=0"`
	IsCompleted    bool                    `json:"isCompleted" validate:"required"`
	Tags           []string                `json:"tags" validate:"min=0,dive,max=32"`
	Comment        string                  `json:"comment" validate:"max=256"`
	Images         []string                `json:"images"`
	PaidAt         time.Time               `json:"paidAt" validate:"required"`
}

// UpdatePayerInPayment - ユーザー毎の支払い情報編集APIのリクエスト
// TODO: 金の更新はいったん無視
type UpdatePayerInPayment struct {
	IsPaid bool `json:"isPaid"`
}
