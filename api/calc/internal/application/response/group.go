package response

import "time"

// CreateGroup - グループ作成APIのレスポンス
type CreateGroup struct {
	ID           string    `json:"id"`
	Name         string    `json:"name"`
	ThumbnailURL string    `json:"thumbnail_url"`
	UserIDs      []string  `json:"user_ids"`
	CreatedAt    time.Time `json:"created_at"`
	UpdatedAt    time.Time `json:"updated_at"`
}
