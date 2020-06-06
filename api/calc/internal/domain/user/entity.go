package user

import "time"

// User - Userエンティティ
type User struct {
	ID           string    `firestore:"id"`            // Firebase AuthenticationのUID
	Name         string    `firestore:"name"`          // 表示名
	Username     string    `firestore:"username"`      // ユーザー名 [unique]
	Email        string    `firestore:"email"`         // メールアドレス
	ThumbnailURL string    `firestore:"thumbnail_url"` // サムネイル
	Password     string    `firestore:"-"`             // パスワード
	CreatedAt    time.Time `firestore:"created_at"`    // 作成日時
	UpdatedAt    time.Time `firestore:"updated_at"`    // 更新日時
}
