package user

import "time"

// User - Userエンティティ
type User struct {
	ID             string    `json:"id"`                    // Firebase AuthenticationのUID
	Name           string    `json:"name"`                  // 表示名
	Username       string    `json:"username"`              // ユーザー名 [unique]
	UsernameLower  string    `firestore:"username_lower"`   // ユーザー名 (すべて小文字)
	Email          string    `json:"email"`                 // メールアドレス
	ThumbnailURL   string    `json:"thumbnailUrl"`          // サムネイル
	GroupIDs       []string  `json:"groupIds"`              // グループID一覧
	HiddenGroupIds []string  `firestore:"hidden_group_ids"` // 非表示グループID一覧
	FriendIDs      []string  `firestore:"friend_ids"`       // 友達のユーザーID一覧
	Password       string    `json:"-"`                     // パスワード
	InstanceID     string    `firestore:"instance_id"`      // 端末のデバイスID
	CreatedAt      time.Time `json:"createdAt"`             // 作成日時
	UpdatedAt      time.Time `json:"updatedAt"`             // 更新日時
}
