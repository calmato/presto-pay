package group

import "time"

// Group - Groupエンティティ
type Group struct {
	ID           string    `firestore:"id"`            // UUID
	Name         string    `firestore:"name"`          // グループ名
	ThumbnailURL string    `firestore:"thumbnail_url"` // サムネイル
	CreatedAt    time.Time `firestore:"created_at"`    // 作成日時
	UpdatedAt    time.Time `firestore:"updated_at"`    // 更新日時
}
