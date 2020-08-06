package response

import "time"

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
