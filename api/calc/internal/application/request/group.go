package request

// CreateGroup - グループ作成APIのリクエスト
type CreateGroup struct {
	Name      string   `json:"name" validate:"required,max=64"`
	Thumbnail string   `json:"thumbnail"`
	UserIDs   []string `json:"userIds"` // TODO: ユニーク検証
}
