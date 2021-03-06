package request

// CreateGroup - グループ作成APIのリクエスト
type CreateGroup struct {
	Name      string   `json:"name" validate:"required,max=64"`
	Thumbnail string   `json:"thumbnail"`
	UserIDs   []string `json:"userIds" validarte:"max=64,unique"`
}

// UpdateGroup - グループ編集APIのリクエスト
type UpdateGroup struct {
	Name      string   `json:"name" validate:"required,max=64"`
	Thumbnail string   `json:"thumbnail"`
	UserIDs   []string `json:"userIds" validarte:"max=64,unique"`
}

// AddUsersInGroup - グループへユーザー追加APIのリクエスト
type AddUsersInGroup struct {
	UserIDs []string `json:"userIds" validate:"max=64,unique"`
}

// UnauthorizedUserInGroup - グループへ追加する未登録ユーザーのリクエスト
type UnauthorizedUserInGroup struct {
	Name      string `json:"name" validate:"required,max=32"`
	Thumbnail string `json:"thumbnail"`
}

// AddUnauthorizedUsersInGroup - グループへ未登録ユーザー追加APIのリクエスト
type AddUnauthorizedUsersInGroup struct {
	Users []*UnauthorizedUserInGroup `json:"users" validate:"max=64,unique"`
}

// RemoveUsersInGroup - グループのユーザー削除APIのリクエスト
type RemoveUsersInGroup struct {
	UserIDs []string `json:"userIds" validate:"max=64,unique"`
}
