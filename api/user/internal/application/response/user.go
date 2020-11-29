package response

import "time"

// IndexUsers - ユーザー一覧取得APIのレスポンス
type IndexUsers struct {
	Users []*ShowUser `json:"users"`
}

// ShowUser - ユーザー取得APIのレスポンス
type ShowUser struct {
	ID           string `json:"id"`
	Name         string `json:"name"`
	Username     string `json:"username"`
	Email        string `json:"email"`
	ThumbnailURL string `json:"thumbnailUrl"`
}

// ShowUserInternal - 内部向けユーザー取得APIのレスポンス
type ShowUserInternal struct {
	ID              string    `json:"id"`
	Name            string    `json:"name"`
	Username        string    `json:"username"`
	Email           string    `json:"email"`
	ThumbnailURL    string    `json:"thumbnailUrl"`
	GroupIDs        []string  `json:"groupIds"`
	HidddenGroupIDs []string  `json:"hiddenGroupIds"`
	FriendIDs       []string  `json:"friendIds"`
	InstanceID      string    `json:"instanceId"`
	CreatedAt       time.Time `json:"createdAt"`
	UpdatedAt       time.Time `json:"updatedAt"`
}

// ShowProfile - ログインユーザー取得APIのレスポンス
type ShowProfile struct {
	ID              string    `json:"id"`
	Name            string    `json:"name"`
	Username        string    `json:"username"`
	Email           string    `json:"email"`
	ThumbnailURL    string    `json:"thumbnailUrl"`
	GroupIDs        []string  `json:"groupIds"`
	HidddenGroupIDs []string  `json:"hiddenGroupIds"`
	FriendIDs       []string  `json:"friendIds"`
	CreatedAt       time.Time `json:"createdAt"`
	UpdatedAt       time.Time `json:"updatedAt"`
}

// CreateUser - ユーザー登録APIのレスポンス
type CreateUser struct {
	ID              string    `json:"id"`
	Name            string    `json:"name"`
	Username        string    `json:"username"`
	Email           string    `json:"email"`
	ThumbnailURL    string    `json:"thumbnailUrl"`
	GroupIDs        []string  `json:"groupIds"`
	HidddenGroupIDs []string  `json:"hiddenGroupIds"`
	FriendIDs       []string  `json:"friendIds"`
	CreatedAt       time.Time `json:"createdAt"`
	UpdatedAt       time.Time `json:"updatedAt"`
}

// CreateUnauthorizedUser - ユーザー登録APIのレスポンス
type CreateUnauthorizedUser struct {
	ID           string    `json:"id"`
	Name         string    `json:"name"`
	ThumbnailURL string    `json:"thumbnailUrl"`
	CreatedAt    time.Time `json:"createdAt"`
	UpdatedAt    time.Time `json:"updatedAt"`
}

// RegisterInstanccID - 端末のデバイスID登録APIのレスポンス
type RegisterInstanccID struct {
	ID              string    `json:"id"`
	Name            string    `json:"name"`
	Username        string    `json:"username"`
	Email           string    `json:"email"`
	ThumbnailURL    string    `json:"thumbnailUrl"`
	GroupIDs        []string  `json:"groupIds"`
	HidddenGroupIDs []string  `json:"hiddenGroupIds"`
	FriendIDs       []string  `json:"friendIds"`
	CreatedAt       time.Time `json:"createdAt"`
	UpdatedAt       time.Time `json:"updatedAt"`
}

// UpdateProfile - ログインユーザー編集APIのレスポンス
type UpdateProfile struct {
	ID              string    `json:"id"`
	Name            string    `json:"name"`
	Username        string    `json:"username"`
	Email           string    `json:"email"`
	ThumbnailURL    string    `json:"thumbnailUrl"`
	GroupIDs        []string  `json:"groupIds"`
	HidddenGroupIDs []string  `json:"hiddenGroupIds"`
	FriendIDs       []string  `json:"friendIds"`
	CreatedAt       time.Time `json:"createdAt"`
	UpdatedAt       time.Time `json:"updatedAt"`
}

// UpdateUnauthorizedUser - ユーザー登録APIのレスポンス
type UpdateUnauthorizedUser struct {
	ID           string    `json:"id"`
	Name         string    `json:"name"`
	ThumbnailURL string    `json:"thumbnailUrl"`
	CreatedAt    time.Time `json:"createdAt"`
	UpdatedAt    time.Time `json:"updatedAt"`
}

// AddGroup - グループ追加APIのレスポンス
type AddGroup struct {
	ID              string    `json:"id"`
	Name            string    `json:"name"`
	Username        string    `json:"username"`
	Email           string    `json:"email"`
	ThumbnailURL    string    `json:"thumbnailUrl"`
	GroupIDs        []string  `json:"groupIds"`
	HidddenGroupIDs []string  `json:"hiddenGroupIds"`
	FriendIDs       []string  `json:"friendIds"`
	InstanceID      string    `json:"instanceId"`
	CreatedAt       time.Time `json:"createdAt"`
	UpdatedAt       time.Time `json:"updatedAt"`
}

// RemoveGroup - グループ削除APIのレスポンス
type RemoveGroup struct {
	ID              string    `json:"id"`
	Name            string    `json:"name"`
	Username        string    `json:"username"`
	Email           string    `json:"email"`
	ThumbnailURL    string    `json:"thumbnailUrl"`
	GroupIDs        []string  `json:"groupIds"`
	HidddenGroupIDs []string  `json:"hiddenGroupIds"`
	InstanceID      string    `json:"instanceId"`
	FriendIDs       []string  `json:"friendIds"`
	CreatedAt       time.Time `json:"createdAt"`
	UpdatedAt       time.Time `json:"updatedAt"`
}

// AddHiddenGroup - 非公開のグループ追加APIのレスポンス
type AddHiddenGroup struct {
	ID              string    `json:"id"`
	Name            string    `json:"name"`
	Username        string    `json:"username"`
	Email           string    `json:"email"`
	ThumbnailURL    string    `json:"thumbnailUrl"`
	GroupIDs        []string  `json:"groupIds"`
	HidddenGroupIDs []string  `json:"hiddenGroupIds"`
	FriendIDs       []string  `json:"friendIds"`
	CreatedAt       time.Time `json:"createdAt"`
	UpdatedAt       time.Time `json:"updatedAt"`
}

// RemoveHiddenGroup - 非公開のグループ削除APIのレスポンス
type RemoveHiddenGroup struct {
	ID              string    `json:"id"`
	Name            string    `json:"name"`
	Username        string    `json:"username"`
	Email           string    `json:"email"`
	ThumbnailURL    string    `json:"thumbnailUrl"`
	GroupIDs        []string  `json:"groupIds"`
	HidddenGroupIDs []string  `json:"hiddenGroupIds"`
	FriendIDs       []string  `json:"friendIds"`
	CreatedAt       time.Time `json:"createdAt"`
	UpdatedAt       time.Time `json:"updatedAt"`
}

// AddFriend - 友達追加APIのレスポンス
type AddFriend struct {
	ID              string    `json:"id"`
	Name            string    `json:"name"`
	Username        string    `json:"username"`
	Email           string    `json:"email"`
	ThumbnailURL    string    `json:"thumbnailUrl"`
	GroupIDs        []string  `json:"groupIds"`
	HidddenGroupIDs []string  `json:"hiddenGroupIds"`
	FriendIDs       []string  `json:"friendIds"`
	CreatedAt       time.Time `json:"createdAt"`
	UpdatedAt       time.Time `json:"updatedAt"`
}

// RemoveFriend - 友達削除APIのレスポンス
type RemoveFriend struct {
	ID              string    `json:"id"`
	Name            string    `json:"name"`
	Username        string    `json:"username"`
	Email           string    `json:"email"`
	ThumbnailURL    string    `json:"thumbnailUrl"`
	GroupIDs        []string  `json:"groupIds"`
	HidddenGroupIDs []string  `json:"hiddenGroupIds"`
	FriendIDs       []string  `json:"friendIds"`
	CreatedAt       time.Time `json:"createdAt"`
	UpdatedAt       time.Time `json:"updatedAt"`
}
