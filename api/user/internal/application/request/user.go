package request

// IndexByUsername - ユーザー名検索によるユーザー一覧取得APIのリクエスト
type IndexByUsername struct {
	Username string `json:"username" validate:"required,max=32"`
	StartAt  string `json:"startAt"` // 検索開始箇所の指定 (もっとみるみたいなの実装したい時用)
}

// CreateUser - ユーザー登録APIのリクエスト
type CreateUser struct {
	Name                 string `json:"name" validate:"required,max=32"`
	Username             string `json:"username" validate:"required,max=32"`
	Email                string `json:"email" validate:"required,email,max=256"`
	Thumbnail            string `json:"thumbnail"`
	Password             string `json:"password" validate:"password,required,min=6,max=32"`
	PasswordConfirmation string `json:"passwordConfirmation" validate:"required,eqfield=Password"`
}

// CreateUnauthorizedUser - 未登録ユーザ作成APIのリクエスト
type CreateUnauthorizedUser struct {
	Name      string `json:"name" validate:"required,max=32"`
	Thumbnail string `json:"thumbnail"`
}

// RegisterInstanceID - 端末のデバイスID登録APIのリクエスト
type RegisterInstanceID struct {
	InstanceID string `json:"instanceId" validate:"required"`
}

// UpdateProfile - ログインユーザー編集APIのリクエスト
type UpdateProfile struct {
	Name      string `json:"name" validate:"required,max=32"`
	Username  string `json:"username" validate:"required,max=32"`
	Email     string `json:"email" validate:"required,email,max=256"`
	Thumbnail string `Json:"thumbnail"`
}

// UpdateUnauthorizedUser - 未登録ユーザ編集APIのリクエスト
type UpdateUnauthorizedUser struct {
	Name      string `json:"name" validate:"required,max=32"`
	Thumbnail string `json:"thumbnail"`
}

// UpdateUserPassword - パスワード編集APIのリクエスト
type UpdateUserPassword struct {
	Password             string `json:"password" validate:"password,required,min=6,max=32"`
	PasswordConfirmation string `json:"passwordConfirmation" validate:"required,eqfield=Password"`
}

// AddFriend - 友達追加APIのリクエスト
type AddFriend struct {
	UserID string `json:"userId" validate:"required"`
}

// UniqueCheckUserEmail - メールアドレスのユニーク検証APIのリクエスト
type UniqueCheckUserEmail struct {
	Email string `json:"email" validate:"required,email,max=256"`
}

// UniqueCheckUserUsername - ユーザー名のユニーク検証APIのリクエスト
type UniqueCheckUserUsername struct {
	Username string `json:"username" validate:"required,max=32"`
}
