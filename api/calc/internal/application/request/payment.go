package request

// PayerInPayment - 支払い者情報
type PayerInPayment struct {
	ID     string  `json:"id" vaidate:"required"`
	Amount float64 `json:"amount" validate:"gt=0,lte=999999"` // TODO: 合計が0になるように
}

// CreatePayment - 支払い情報作成APIのリクエスト
type CreatePayment struct {
	Name           string            `json:"name" validate:"required,max=64"`
	Currency       string            `json:"currency" validate:"required,currency"`
	PositivePayers []*PayerInPayment `json:"positivePayers" validate:"min=1,max=64"`
	NegativePayers []*PayerInPayment `json:"negativePayers" validate:"min=1,max=64"`
	Tags           []string          `json:"tags" validate:"min=0,max=32,dive,max=32"`
	Comment        string            `json:"comment" validate:"max=256"`
	Images         []string          `json:"images" validate:"min=0,max=32"`
	PaidAt         string            `json:"paidAt" validate:"required,datetime"`
}

// UpdatePayment - 支払い情報作成APIのリクエスト
type UpdatePayment struct {
	Name           string            `json:"name" validate:"required,max=64"`
	Currency       string            `json:"currency" validate:"required,currency"`
	Total          float64           `json:"total" validate:"gte=0,lte=9999999"`
	PositivePayers []*PayerInPayment `json:"positivePayers" validate:"min=1,max=64"`
	NegativePayers []*PayerInPayment `json:"negativePayers" validate:"min=1,max=64"`
	Tags           []string          `json:"tags" validate:"min=0,max=32,dive,max=32"`
	Comment        string            `json:"comment" validate:"max=256"`
	Images         []string          `json:"images" validate:"min=0,max=32"`
	PaidAt         string            `json:"paidAt" validate:"required,datetime"`
}

// UpdatePayerInPayment - ユーザー毎の支払い情報編集APIのリクエスト
type UpdatePayerInPayment struct {
	IsPaid bool `json:"isPaid" validate:"oneof=true false"`
}
