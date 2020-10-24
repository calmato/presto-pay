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
	ThumbnailURL string             `json:"thumbnailUrl"`
	Users        []*UserToShowGroup `json:"users"`
	CreatedAt    time.Time          `json:"createdAt"`
	UpdatedAt    time.Time          `json:"updatedAt"`
}

// IndexGroup - グループ一覧取得APIのグループ情報
type IndexGroup struct {
	ID           string    `json:"id"`
	Name         string    `json:"name"`
	ThumbnailURL string    `json:"thumbnailUrl"`
	UserIDs      []string  `json:"userIds"`
	CreatedAt    time.Time `json:"createdAt"`
	UpdatedAt    time.Time `json:"updatedAt"`
}

// IndexGroups - グループ一覧取得APIのレスポンス
type IndexGroups struct {
	Groups       []*IndexGroup `json:"groups"`
	HiddenGroups []*IndexGroup `json:"hiddenGroups"`
}

// CreateGroup - グループ作成APIのレスポンス
type CreateGroup struct {
	ID           string    `json:"id"`
	Name         string    `json:"name"`
	ThumbnailURL string    `json:"thumbnailUrl"`
	UserIDs      []string  `json:"userIds"`
	CreatedAt    time.Time `json:"createdAt"`
	UpdatedAt    time.Time `json:"updatedAt"`
}

// UpdateGroup - グループ編集APIのレスポンス
type UpdateGroup struct {
	ID           string    `json:"id"`
	Name         string    `json:"name"`
	ThumbnailURL string    `json:"thumbnailUrl"`
	UserIDs      []string  `json:"userIds"`
	CreatedAt    time.Time `json:"createdAt"`
	UpdatedAt    time.Time `json:"updatedAt"`
}

// AddUsersInGroup - グループへユーザー追加APIのレスポンス
type AddUsersInGroup struct {
	ID           string    `json:"id"`
	Name         string    `json:"name"`
	ThumbnailURL string    `json:"thumbnailUrl"`
	UserIDs      []string  `json:"userIds"`
	CreatedAt    time.Time `json:"createdAt"`
	UpdatedAt    time.Time `json:"updatedAt"`
}

// RemoveUsersInGroup - グループのユーザー削除APIのレスポンス
type RemoveUsersInGroup struct {
	ID           string    `json:"id"`
	Name         string    `json:"name"`
	ThumbnailURL string    `json:"thumbnailUrl"`
	UserIDs      []string  `json:"userIds"`
	CreatedAt    time.Time `json:"createdAt"`
	UpdatedAt    time.Time `json:"updatedAt"`
}
