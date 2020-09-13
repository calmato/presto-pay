package response

import "time"

// PayerInIndexPayments - 支払い情報一覧の支払い者情報
type PayerInIndexPayments struct {
	ID     string  `json:"id"`
	Name   string  `json:"name"`
	Amount float64 `json:"amount"`
}

// PaymentInIndexPayments - 支払い情報一覧取得の支払い情報
type PaymentInIndexPayments struct {
	ID        string                  `json:"id"`
	Name      string                  `json:"name"`
	Currency  string                  `json:"currency"`
	Total     float64                 `json:"total"`
	Payers    []*PayerInIndexPayments `json:"payers"`
	Tags      []string                `json:"tags"`
	Comment   string                  `json:"comment"`
	ImageURLs []string                `json:"imageUrls"`
	PaidAt    time.Time               `json:"paidAt"`
	CreatedAt time.Time               `json:"createdAt"`
	UpdatedAt time.Time               `json:"updatedAt"`
}

// IndexPayments - 支払い情報一覧取得APIのレスポンス
type IndexPayments struct {
	Payments []*PaymentInIndexPayments `json:"payments"`
}

// PayerInCreatePayment - 支払い情報作成の支払い者情報
type PayerInCreatePayment struct {
	ID     string  `json:"id"`
	Amount float64 `json:"amount"`
}

// CreatePayment - 支払い情報作成APIのレスポンス
type CreatePayment struct {
	ID        string                  `json:"id"`
	Name      string                  `json:"name"`
	Currency  string                  `json:"currency"`
	Total     float64                 `json:"total"`
	Payers    []*PayerInCreatePayment `json:"payers"`
	Tags      []string                `json:"tags"`
	Comment   string                  `json:"comment"`
	ImageURLs []string                `json:"imageUrls"`
	PaidAt    time.Time               `json:"paidAt"`
	CreatedAt time.Time               `json:"createdAt"`
	UpdatedAt time.Time               `json:"updatedAt"`
}
