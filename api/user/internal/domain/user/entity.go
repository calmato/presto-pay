package user

import "time"

// User - Userエンティティ
type User struct {
	ID             string    `firestore:"id"`               // Firebase AuthenticationのUID
	Name           string    `firestore:"name"`             // 表示名
	Username       string    `firestore:"username"`         // ユーザー名 [unique]
	UsernameLower  string    `firestore:"username_lower"`   // ユーザー名 (すべて小文字)
	Email          string    `firestore:"email"`            // メールアドレス
	ThumbnailURL   string    `firestore:"thumbnail_url"`    // サムネイル
	GroupIDs       []string  `firestore:"group_ids"`        // グループID一覧
	HiddenGroupIDs []string  `firestore:"hidden_group_ids"` // 非表示グループID一覧
	FriendIDs      []string  `firestore:"friend_ids"`       // 友達のユーザーID一覧
	Password       string    `firestore:"-"`                // パスワード
	InstanceID     string    `firestore:"instance_id"`      // 端末のデバイスID
	CreatedAt      time.Time `firestore:"created_at"`       // 作成日時
	UpdatedAt      time.Time `firestore:"updated_at"`       // 更新日時
}
