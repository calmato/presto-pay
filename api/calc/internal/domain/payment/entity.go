package payment

import (
	"time"
)

// Payment - 支払い エンティティ
type Payment struct {
	ID          string    `firestore:"id"`           // UUID
	GroupID     string    `firestore:"-"`            // グループUUID
	Name        string    `firestore:"name"`         // 支払い名
	Currency    string    `firestore:"currency"`     // 通貨
	Total       float64   `firestore:"total"`        // 合計金額
	Payers      []*Payer  `firestore:"payers"`       // 支払い者情報一覧
	Tags        []string  `firestore:"tags"`         // タグ
	Comment     string    `firestore:"comment"`      // コメント
	ImageURLs   []string  `firestore:"image_urls"`   // 添付イメージURL一覧
	PaidAt      time.Time `firestore:"paid_at"`      // 支払い日時
	IsCompleted bool      `firestore:"is_completed"` // 支払い完了フラグ (true: 全ての支払いが完了)
	CreatedAt   time.Time `firestore:"created_at"`   // 作成日時
	UpdatedAt   time.Time `firestore:"updated_at"`   // 更新日時
}

// Payer - 支払い者 エンティティ
type Payer struct {
	ID     string    `firestore:"id"`      // ユーザーID
	Name   string    `firestore:"-"`       // ユーザー名
	Amount float64   `firestore:"amount"`  // 支払い金額
	IsPaid bool      `firestore:"is_paid"` // 支払いフラグ (true: 受け取る側, false: 支払う側)
	PaidAt time.Time `firestore:"paid_at"` // 支払い完了日時
}
