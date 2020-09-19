package response

import "time"

// PayerInIndexPayments - 支払い情報一覧の支払い者情報
type PayerInIndexPayments struct {
	ID     string  `json:"id"`
	Name   string  `json:"name"`
	Amount float64 `json:"amount"`
	IsPaid bool    `json:"isPaid"`
}

// PaymentInIndexPayments - 支払い情報一覧取得の支払い情報
type PaymentInIndexPayments struct {
	ID          string                  `json:"id"`
	Name        string                  `json:"name"`
	Currency    string                  `json:"currency"`
	Total       float64                 `json:"total"`
	Payers      []*PayerInIndexPayments `json:"payers"`
	Tags        []string                `json:"tags"`
	Comment     string                  `json:"comment"`
	ImageURLs   []string                `json:"imageUrls"`
	PaidAt      time.Time               `json:"paidAt"`
	IsCompleted bool                    `json:"isCompleted"`
	CreatedAt   time.Time               `json:"createdAt"`
	UpdatedAt   time.Time               `json:"updatedAt"`
}

// IndexPayments - 支払い情報一覧取得APIのレスポンス
type IndexPayments struct {
	Payments []*PaymentInIndexPayments `json:"payments"`
}

// PayerInCreatePayment - 支払い情報作成の支払い者情報
type PayerInCreatePayment struct {
	ID     string  `json:"id"`
	Amount float64 `json:"amount"`
	IsPaid bool    `json:"isPaid"`
}

// CreatePayment - 支払い情報作成APIのレスポンス
type CreatePayment struct {
	ID          string                  `json:"id"`
	Name        string                  `json:"name"`
	Currency    string                  `json:"currency"`
	Total       float64                 `json:"total"`
	Payers      []*PayerInCreatePayment `json:"payers"`
	Tags        []string                `json:"tags"`
	Comment     string                  `json:"comment"`
	ImageURLs   []string                `json:"imageUrls"`
	PaidAt      time.Time               `json:"paidAt"`
	IsCompleted bool                    `json:"isCompleted"`
	CreatedAt   time.Time               `json:"createdAt"`
	UpdatedAt   time.Time               `json:"updatedAt"`
}

// PayerInUpdatePayment - 支払い情報更新の支払い者情報
type PayerInUpdatePayment struct {
	ID     string  `json:"id"`
	Amount float64 `json:"amount"`
	IsPaid bool    `json:"isPaid"`
}

// UpdatePayment - 支払い情報更新APIのレスポンス
type UpdatePayment struct {
	ID          string                  `json:"id"`
	Name        string                  `json:"name"`
	Currency    string                  `json:"currency"`
	Total       float64                 `json:"total"`
	Payers      []*PayerInUpdatePayment `json:"payers"`
	Tags        []string                `json:"tags"`
	Comment     string                  `json:"comment"`
	ImageURLs   []string                `json:"imageUrls"`
	PaidAt      time.Time               `json:"paidAt"`
	IsCompleted bool                    `json:"isCompleted"`
	CreatedAt   time.Time               `json:"createdAt"`
	UpdatedAt   time.Time               `json:"updatedAt"`
}

// UpdatePayments - 支払い情報一括更新APIのレスポンス
type UpdatePayments struct {
	Payments []*UpdatePayment `json:"payments"`
}
