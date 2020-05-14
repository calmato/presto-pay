package request

// CreateUser - ユーザー登録APIのリクエスト
type CreateUser struct {
	Name                 string `json:"name" validate:"required,max=32"`
	Username             string `json:"username" validate:"required,max=32"`
	Email                string `json:"email" validate:"required,email,max=256"`
	Thumbnail            string `json:"thumbnail"`
	Password             string `json:"password" validate:"password,required,min=6,max=32"`
	PasswordConfirmation string `json:"passwordConfirmation" validate:"required,eqfield=Password"`
}

// UpdateUser - ユーザー編集APIのリクエスト
type UpdateUser struct {
	Name      string `json:"name" validate:"required,max=32"`
	Username  string `json:"username" validate:"required,max=32"`
	Email     string `json:"email" validate:"required,email,max=256"`
	Thumbnail string `Json:"thumbnail"`
	Language  string `json:"language" validate:"required"`
}
