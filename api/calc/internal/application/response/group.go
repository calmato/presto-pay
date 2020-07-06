package response

import (
	"time"
)

// UserToShowGroup - グループ詳細のユーザー情報
type UserToShowGroup struct {
	ID           string `json:"id"`
	Name         string `json:"name"`
	Username     string `json:"username"`
	Email        string `json:"email"`
	ThumbnailURL string `json:"thumbnailUrl"`
}

// ShowGroup - グループ詳細取得APIのレスポンス
type ShowGroup struct {
	ID           string             `json:"id"`
	Name         string             `json:"name"`
	ThumbnailURL string             `json:"thumbnail_url"`
	Users        []*UserToShowGroup `json:"users"`
	CreatedAt    time.Time          `json:"created_at"`
	UpdatedAt    time.Time          `json:"updated_at"`
}

// IndexGroup - グループ一覧取得APIのグループ情報
type IndexGroup struct {
	ID           string    `json:"id"`
	Name         string    `json:"name"`
	ThumbnailURL string    `json:"thumbnail_url"`
	UserIDs      []string  `json:"userIds"`
	CreatedAt    time.Time `json:"created_at"`
	UpdatedAt    time.Time `json:"updated_at"`
}

// IndexGroups - グループ一覧取得APIのレスポンス
type IndexGroups struct {
	Groups []*IndexGroup `json:"groups"`
}

// CreateGroup - グループ作成APIのレスポンス
type CreateGroup struct {
	ID           string    `json:"id"`
	Name         string    `json:"name"`
	ThumbnailURL string    `json:"thumbnail_url"`
	UserIDs      []string  `json:"user_ids"`
	CreatedAt    time.Time `json:"created_at"`
	UpdatedAt    time.Time `json:"updated_at"`
}
