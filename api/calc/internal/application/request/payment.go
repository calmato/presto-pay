package request

import "time"

// PayerInCreatePayment - 支払い情報作成用 支払い者情報
type PayerInCreatePayment struct {
	ID     string  `json:"id" vaidate:"required"`
	Amount float64 `json:"amount" validate:"gt=0,lte=999999"` // TODO: 合計が0になるように
}

// CreatePayment - 支払い情報作成APIのリクエスト
type CreatePayment struct {
	Name           string                  `json:"name" validate:"required,max=64"`
	Currency       string                  `json:"currency" validate:"required,currency"`
	PositivePayers []*PayerInCreatePayment `json:"positivePayers" validate:"min=0,max=64"`
	NegativePayers []*PayerInCreatePayment `json:"negativePayers" validate:"min=0,max=64"`
	Tags           []string                `json:"tags" validate:"min=0,max=32,dive,max=32"`
	Comment        string                  `json:"comment" validate:"max=256"`
	Images         []string                `json:"images" validate:"min=0,max=32"`
	PaidAt         time.Time               `json:"paidAt" validate:"required,datetime"`
}

// PayerInUpdatePayment - 支払い情報編集用 支払い者情報
type PayerInUpdatePayment struct {
	ID     string  `json:"id" validate:"required"`
	Amount float64 `json:"amount" validate:"gt=0,lte=999999"` // TODO: 合計が0になるように
	IsPaid bool    `json:"isPaid" validate:"oneof=true false"`
}

// UpdatePayment - 支払い情報作成APIのリクエスト
type UpdatePayment struct {
	Name           string                  `json:"name" validate:"required,max=64"`
	Currency       string                  `json:"currency" validate:"required,currency"`
	PositivePayers []*PayerInUpdatePayment `json:"positivePayers" validate:"min=0,max=64"`
	NegativePayers []*PayerInUpdatePayment `json:"negativePayers" validate:"min=0,max=64"`
	IsCompleted    bool                    `json:"isCompleted" validate:"oneof=true false"`
	Tags           []string                `json:"tags" validate:"min=0,max=32,dive,max=32"`
	Comment        string                  `json:"comment" validate:"max=256"`
	Images         []string                `json:"images" validate:"min=0,max=32"`
	PaidAt         time.Time               `json:"paidAt" validate:"required,datetime"`
}

// UpdatePayerInPayment - ユーザー毎の支払い情報編集APIのリクエスト
// TODO: 金の更新はいったん無視
type UpdatePayerInPayment struct {
	IsPaid bool `json:"isPaid" validate:"oneof=true false"`
}
