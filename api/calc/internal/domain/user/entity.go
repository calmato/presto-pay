package user

import "time"

// User - Userエンティティ
type User struct {
	ID           string    `json:"id"`            // Firebase AuthenticationのUID
	Name         string    `json:"name"`          // 表示名
	Username     string    `json:"username"`      // ユーザー名 [unique]
	Email        string    `json:"email"`         // メールアドレス
	ThumbnailURL string    `json:"thumbnail_url"` // サムネイル
	Password     string    `json:"-"`             // パスワード
	CreatedAt    time.Time `json:"created_at"`    // 作成日時
	UpdatedAt    time.Time `json:"updated_at"`    // 更新日時
}
