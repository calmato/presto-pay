package response

import "time"

// ShowUser - ユーザー取得APIのレスポンス
type ShowUser struct {
	ID           string   `json:"id"`
	Name         string   `json:"name"`
	Username     string   `json:"username"`
	Email        string   `json:"email"`
	ThumbnailURL string   `json:"thumbnailUrl"`
	GroupIDs     []string `json:"groupIds"`
}

// ShowProfile - ログインユーザー取得APIのレスポンス
type ShowProfile struct {
	ID           string    `json:"id"`
	Name         string    `json:"name"`
	Username     string    `json:"username"`
	Email        string    `json:"email"`
	ThumbnailURL string    `json:"thumbnailUrl"`
	GroupIDs     []string  `json:"groupIds"`
	CreatedAt    time.Time `json:"createdAt"`
	UpdatedAt    time.Time `json:"updatedAt"`
}

// SearchUsers - ユーザー検索APIのレスポンス
type SearchUsers struct {
	Users []*ShowUser `json:"users"`
}

// CreateUser - ユーザー登録APIのレスポンス
type CreateUser struct {
	ID           string    `json:"id"`
	Name         string    `json:"name"`
	Username     string    `json:"username"`
	Email        string    `json:"email"`
	ThumbnailURL string    `json:"thumbnailUrl"`
	GroupIDs     []string  `json:"groupIds"`
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
	GroupIDs     []string  `json:"groupIds"`
	CreatedAt    time.Time `json:"createdAt"`
	UpdatedAt    time.Time `json:"updatedAt"`
}

// AddGroupUser - グループ追加APIのレスポンス
type AddGroupUser struct {
	ID           string    `json:"id"`
	Name         string    `json:"name"`
	Username     string    `json:"username"`
	Email        string    `json:"email"`
	ThumbnailURL string    `json:"thumbnailUrl"`
	GroupIDs     []string  `json:"groupIds"`
	CreatedAt    time.Time `json:"createdAt"`
	UpdatedAt    time.Time `json:"updatedAt"`
}

// RemoveGroupUser - グループ削除APIのレスポンス
type RemoveGroupUser struct {
	ID           string    `json:"id"`
	Name         string    `json:"name"`
	Username     string    `json:"username"`
	Email        string    `json:"email"`
	ThumbnailURL string    `json:"thumbnailUrl"`
	GroupIDs     []string  `json:"groupIds"`
	CreatedAt    time.Time `json:"createdAt"`
	UpdatedAt    time.Time `json:"updatedAt"`
}
