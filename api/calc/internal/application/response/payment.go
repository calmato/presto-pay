package response

import "time"

// PayerInPayment - 支払い者情報
type PayerInPayment struct {
	ID     string  `json:"id"`
	Name   string  `json:"name"`
	Amount float64 `json:"amount"`
	IsPaid bool    `json:"isPaid"`
}

// Payment - 支払い情報
type Payment struct {
	ID             string            `json:"id"`
	GroupID        string            `json:"groupId"`
	Name           string            `json:"name"`
	Currency       string            `json:"currency"`
	Total          float64           `json:"total"`
	Payers         []*PayerInPayment `json:"payers"`
	PostivePayers  []*PayerInPayment `json:"postivePayers"`
	NegativePayers []*PayerInPayment `json:"negativePayers"`
	Tags           []string          `json:"tags"`
	Comment        string            `json:"comment"`
	ImageURLs      []string          `json:"imageUrls"`
	PaidAt         time.Time         `json:"paidAt"`
	IsCompleted    bool              `json:"isCompleted"`
	CreatedAt      time.Time         `json:"createdAt"`
	UpdatedAt      time.Time         `json:"updatedAt"`
}

// UserInPayments - ユーザー毎の支払い情報
type UserInPayments struct {
	ID     string  `json:"id"`
	Name   string  `json:"name"`
	Amount float64 `json:"amount"`
}

// Payments - 支払い情報一覧
type Payments struct {
	Payments []*Payment                 `json:"payments"`
	Users    map[string]*UserInPayments `json:"users"`
	Currency string                     `json:"currency"`
}
