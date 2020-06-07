package response

import "time"

// CreateUser - ユーザー登録APIのレスポンス
type CreateUser struct {
	ID           string    `json:"id"`
	Name         string    `json:"name"`
	Username     string    `json:"username"`
	Email        string    `json:"email"`
	ThumbnailURL string    `json:"thumbnailUrl"`
	CreatedAt    time.Time `json:"createdAt"`
	UpdatedAt    time.Time `json:"updatedAt"`
}

// UpdateProfile - ログインユーザー編集APIのレスポンス
type UpdateProfile struct {
	ID           string    `json:"id"`
	Name         string    `json:"name"`
	Username     string    `json:"username"`
	Email        string    `json:"email"`
	ThumbnailURL string    `json:"thumbnailUrl"`
	CreatedAt    time.Time `json:"createdAt"`
	UpdatedAt    time.Time `json:"updatedAt"`
}
