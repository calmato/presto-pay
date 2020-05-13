package request

// CreateUser - ユーザー登録APIのリクエスト
type CreateUser struct {
	Name                 string `json:"name" label:"name" validate:"required,max=32"`
	DisplayName          string `json:"displayName" label:"displayName" validate:"required,max=32"`
	Email                string `json:"email" label:"email" validate:"required,email,max=256"`
	Thumbnail            string `json:"thumbnail" label:"thumbnail"`
	Password             string `json:"password" label:"password" validate:"required,password,min=6,max=32"`
	PasswordConfirmation string `json:"passwordConfirmation" label:"passwordConfirmation" validate:"required,eqfield=Password"`
}
