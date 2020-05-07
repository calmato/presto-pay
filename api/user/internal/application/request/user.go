package request

// CreateUser - ユーザー登録APIのリクエスト
type CreateUser struct {
	Name                 string `json:"name" label:"氏名" validate:"required,max=32"`
	DisplayName          string `json:"displayName" label:"表示名" validate:"required,max=32"`
	Email                string `json:"email" label:"メールアドレス" validate:"required,email,max=256"`
	Thumbnail            string `json:"thumbnail" label:"サムネイル"`
	Password             string `json:"password" label:"パスワード" validate:"required,password,min=6,max=32"`
	PasswordConfirmation string `json:"passwordConfirmation" label:"パスワード(確認用)" validate:"required,eqfield=Password"`
}
