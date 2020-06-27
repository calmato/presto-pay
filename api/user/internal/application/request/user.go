package request

// SearchUsersByUsername - ユーザー名でのユーザー検索のリクエスト
type SearchUsersByUsername struct {
	Username string `json:"username" validate:"required,max=32"`
	StartAt  string `json:"startAt"` // 検索開始箇所の指定 (もっとみるみたいなの実装した時用)
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

// UpdateProfile - ログインユーザー編集APIのリクエスト
type UpdateProfile struct {
	Name      string `json:"name" validate:"required,max=32"`
	Username  string `json:"username" validate:"required,max=32"`
	Email     string `json:"email" validate:"required,email,max=256"`
	Thumbnail string `Json:"thumbnail"`
}

// UpdateUserPassword - パスワード編集APIのリクエスト
type UpdateUserPassword struct {
	Password             string `json:"password" validate:"password,required,min=6,max=32"`
	PasswordConfirmation string `json:"passwordConfirmation" validate:"required,eqfield=Password"`
}

// UniqueCheckUserEmail - メールアドレスのユニーク検証APIのリクエスト
type UniqueCheckUserEmail struct {
	Email string `json:"email" validate:"required,email,max=256"`
}

// UniqueCheckUserUsername - ユーザー名のユニーク検証APIのリクエスト
type UniqueCheckUserUsername struct {
	Username string `json:"username" validate:"required,max=32"`
}
