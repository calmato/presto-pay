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
	ID             string                  `json:"id"`
	GroupID        string                  `json:"groupId"`
	Name           string                  `json:"name"`
	Currency       string                  `json:"currency"`
	Total          float64                 `json:"total"`
	Payers         []*PayerInIndexPayments `json:"payers"`
	PostivePayers  []*PayerInIndexPayments `json:"postivePayers"`
	NegativePayers []*PayerInIndexPayments `json:"negativePayers"`
	Tags           []string                `json:"tags"`
	Comment        string                  `json:"comment"`
	ImageURLs      []string                `json:"imageUrls"`
	PaidAt         time.Time               `json:"paidAt"`
	IsCompleted    bool                    `json:"isCompleted"`
	CreatedAt      time.Time               `json:"createdAt"`
	UpdatedAt      time.Time               `json:"updatedAt"`
}

// UserInIndexPayments - ユーザー毎の支払い情報
type UserInIndexPayments struct {
	ID     string  `json:"id"`
	Name   string  `json:"name"`
	Amount float64 `json:"amount"`
}

// IndexPayments - 支払い情報一覧取得APIのレスポンス
type IndexPayments struct {
	Payments []*PaymentInIndexPayments       `json:"payments"`
	Users    map[string]*UserInIndexPayments `json:"users"`
	Currency string                          `json:"currency"`
}

// PayerInCreatePayment - 支払い情報作成の支払い者情報
type PayerInCreatePayment struct {
	ID     string  `json:"id"`
	Amount float64 `json:"amount"`
}

// CreatePayment - 支払い情報作成APIのレスポンス
type CreatePayment struct {
	ID             string                  `json:"id"`
	GroupID        string                  `json:"groupId"`
	Name           string                  `json:"name"`
	Currency       string                  `json:"currency"`
	Total          float64                 `json:"total"`
	Payers         []*PayerInCreatePayment `json:"payers"`
	PostivePayers  []*PayerInCreatePayment `json:"postivePayers"`
	NegativePayers []*PayerInCreatePayment `json:"negativePayers"`
	Tags           []string                `json:"tags"`
	Comment        string                  `json:"comment"`
	ImageURLs      []string                `json:"imageUrls"`
	PaidAt         time.Time               `json:"paidAt"`
	IsCompleted    bool                    `json:"isCompleted"`
	CreatedAt      time.Time               `json:"createdAt"`
	UpdatedAt      time.Time               `json:"updatedAt"`
}

// PayerInUpdatePayment - 支払い情報更新の支払い者情報
type PayerInUpdatePayment struct {
	ID     string  `json:"id"`
	Amount float64 `json:"amount"`
}

// UpdatePayment - 支払い情報更新APIのレスポンス
type UpdatePayment struct {
	ID             string                  `json:"id"`
	GroupID        string                  `json:"groupId"`
	Name           string                  `json:"name"`
	Currency       string                  `json:"currency"`
	Total          float64                 `json:"total"`
	Payers         []*PayerInUpdatePayment `json:"payers"`
	PostivePayers  []*PayerInUpdatePayment `json:"postivePayers"`
	NegativePayers []*PayerInUpdatePayment `json:"negativePayers"`
	Tags           []string                `json:"tags"`
	Comment        string                  `json:"comment"`
	ImageURLs      []string                `json:"imageUrls"`
	PaidAt         time.Time               `json:"paidAt"`
	IsCompleted    bool                    `json:"isCompleted"`
	CreatedAt      time.Time               `json:"createdAt"`
	UpdatedAt      time.Time               `json:"updatedAt"`
}

// UpdatePayments - 支払い情報一括更新APIのレスポンス
type UpdatePayments struct {
	Payments []*UpdatePayment `json:"payments"`
}
