package group

import (
	"time"

	"github.com/calmato/presto-pay/api/calc/internal/domain/user"
)

// Group - Groupエンティティ
type Group struct {
	ID           string       `firestore:"id"`            // UUID
	Name         string       `firestore:"name"`          // グループ名
	ThumbnailURL string       `firestore:"thumbnail_url"` // サムネイル
	UserIDs      []string     `firestore:"user_ids"`      // 参加ユーザーID一覧
	Users        []*user.User `firestore:"-"`             // 参加ユーザー一覧
	CreatedAt    time.Time    `firestore:"created_at"`    // 作成日時
	UpdatedAt    time.Time    `firestore:"updated_at"`    // 更新日時
}
