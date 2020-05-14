package domain

// CustomError - エラーコードを含めた構造体
type CustomError struct {
	ErrorCode        ErrorCode
	Value            error
	ValidationErrors []*ValidationError
}

// ValidationError - バリデーションエラー用構造体
type ValidationError struct {
	Field   string
	Message string
}

// ErrorCode - システムエラーの種類
type ErrorCode uint

const (
	// Unknown - 不明なエラー
	Unknown ErrorCode = iota
	// Unauthorized - 認証エラー
	Unauthorized
	// Forbidden - 権限エラー
	Forbidden
	// NotFound - 取得エラー
	NotFound
	// InvalidDomainValidation - ドメインのバリデーションエラー
	InvalidDomainValidation
	// InvalidRequestValidation - リクエストのバリデーションエラー
	InvalidRequestValidation
	// UnableParseJSON - JSON型から構造体への変換エラー
	UnableParseJSON
	// UnableConvertBase64 - Byte64型への変換エラー
	UnableConvertBase64
	// ErrorInDatastore - データストアでのエラー
	ErrorInDatastore
	// AlreadyExistsInDatastore - ユニークチェックでのエラー
	AlreadyExistsInDatastore
	// NotEqualRequestWithDatastore - リクエスト値がデータストアの値と一致しない
	NotEqualRequestWithDatastore
	// ErrorInStorage - ストレージでのエラー
	ErrorInStorage
)

// New - 指定したErrorCodeを持つCustomErrorを返す
func (ec ErrorCode) New(err error, ves ...*ValidationError) error {
	return CustomError{
		ErrorCode:        ec,
		Value:            err,
		ValidationErrors: ves,
	}
}

// Error - エラー内容を返す
func (ce CustomError) Error() string {
	return ce.Value.Error()
}

// Code - エラーコードを返す
func (ce CustomError) Code() ErrorCode {
	return ce.ErrorCode
}

// Validations - バリデーションエラーの詳細を返す
func (ce CustomError) Validations() []*ValidationError {
	return ce.ValidationErrors
}
