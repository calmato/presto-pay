package response

import "time"

// IndexUsers - ユーザー一覧取得APIのレスポンス
type IndexUsers struct {
	Users []*ShowUser `json:"users"`
}

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
	FriendIDs    []string  `json:"friendIds"`
	CreatedAt    time.Time `json:"createdAt"`
	UpdatedAt    time.Time `json:"updatedAt"`
}

// CreateUser - ユーザー登録APIのレスポンス
type CreateUser struct {
	ID           string    `json:"id"`
	Name         string    `json:"name"`
	Username     string    `json:"username"`
	Email        string    `json:"email"`
	ThumbnailURL string    `json:"thumbnailUrl"`
	GroupIDs     []string  `json:"groupIds"`
	FriendIDs    []string  `json:"friendIds"`
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
	FriendIDs    []string  `json:"friendIds"`
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
	FriendIDs    []string  `json:"friendIds"`
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
	FriendIDs    []string  `json:"friendIds"`
	CreatedAt    time.Time `json:"createdAt"`
	UpdatedAt    time.Time `json:"updatedAt"`
}

// AddFriend - 友達追加APIのレスポンス
type AddFriend struct {
	ID           string    `json:"id"`
	Name         string    `json:"name"`
	Username     string    `json:"username"`
	Email        string    `json:"email"`
	ThumbnailURL string    `json:"thumbnailUrl"`
	GroupIDs     []string  `json:"groupIds"`
	FriendIDs    []string  `json:"friendIds"`
	CreatedAt    time.Time `json:"createdAt"`
	UpdatedAt    time.Time `json:"updatedAt"`
}
