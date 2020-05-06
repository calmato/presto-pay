package response

import (
	"net/http"

	"github.com/calmato/presto-pay/api/user/internal/domain"
)

// ValidationError - バリデーションエラーのレスポンス
type ValidationError struct {
	Field   string `json:"field"`
	Message string `json:"message"`
}

// ErrorResponse - エラーのレスポンス
type ErrorResponse struct {
	StatusCode       int                `json:"status"`
	ErrorCode        domain.ErrorCode   `json:"code"`
	Message          string             `json:"message"`
	ValidationErrors []*ValidationError `json:"errors,omitempty"`
}

// ステータスコードを付与したエラーレスポンス
var (
	BadRequest = &ErrorResponse{
		StatusCode:       http.StatusBadRequest, // 400
		Message:          "不正なパラメータが入力されています。",
		ValidationErrors: nil,
	}

	Unauthorized = &ErrorResponse{
		StatusCode:       http.StatusUnauthorized, // 401
		Message:          "認証に必要な情報がありません。",
		ValidationErrors: nil,
	}

	Forbidden = &ErrorResponse{
		StatusCode:       http.StatusForbidden, // 403
		Message:          "その操作を実行する権限がありません。",
		ValidationErrors: nil,
	}

	NotFound = &ErrorResponse{
		StatusCode:       http.StatusNotFound, // 404
		Message:          "指定の情報が見つかりません。",
		ValidationErrors: nil,
	}

	AlreadyExists = &ErrorResponse{
		StatusCode:       http.StatusConflict, // 409
		Message:          "不正なパラメータが入力されています。",
		ValidationErrors: nil,
	}

	InternalServerError = &ErrorResponse{
		StatusCode:       http.StatusInternalServerError, // 500
		Message:          "異常な処理が検出されました。",
		ValidationErrors: nil,
	}
)
