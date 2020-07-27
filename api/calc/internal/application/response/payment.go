package response

import "time"

// PayerInCreatePayment - 支払い情報作成の支払い者情報
type PayerInCreatePayment struct {
	ID     string `json:"id"`
	Amount int    `json:"amount"`
}

// CreatePayment - 支払い情報作成APIのレスポンス
type CreatePayment struct {
	ID        string                 `json:"id"`
	Name      string                 `json:"name"`
	Currency  string                 `json:"currency"`
	Total     uint                   `json:"total"`
	Payers    []PayerInCreatePayment `json:"payers"`
	Tags      []string               `json:"tags"`
	Comment   string                 `json:"comment"`
	ImageURLs []string               `json:"image_urls"`
	PaidAt    time.Time              `json:"paid_at"`
	CreatedAt time.Time              `json:"created_at"`
	UpdatedAt time.Time              `json:"updated_at"`
}
