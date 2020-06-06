package response

import (
	"net/http"

	"github.com/calmato/presto-pay/api/calc/internal/domain"
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

// エラーメッセージ
const (
	badRequestMessage = `The API request is invalid or improperly formed. Consequently,
the API server could not understand the request.`
	unauthorizedMessage = "The user is not authorized to make the request."
	forbiddenMessage    = "The requested operation is forbidden and cannot be completed."
	notFoundMessage     = `The requested operation failed
because a resource associated with the request could not be found.`
	alreadyExistsMessage = `The API request cannot be completed
because the requested operation would conflict with an existing item.
For example, a request that tries to create a duplicate item would create a conflict,
though duplicate items are typically identified with more specific errors.`
	internalServerErrorMessage = "The request failed due to an internal error."
)

// ステータスコードを付与したエラーレスポンス
var (
	BadRequest = &ErrorResponse{
		StatusCode:       http.StatusBadRequest, // 400
		Message:          badRequestMessage,
		ValidationErrors: nil,
	}

	Unauthorized = &ErrorResponse{
		StatusCode:       http.StatusUnauthorized, // 401
		Message:          unauthorizedMessage,
		ValidationErrors: nil,
	}

	Forbidden = &ErrorResponse{
		StatusCode:       http.StatusForbidden, // 403
		Message:          forbiddenMessage,
		ValidationErrors: nil,
	}

	NotFound = &ErrorResponse{
		StatusCode:       http.StatusNotFound, // 404
		Message:          notFoundMessage,
		ValidationErrors: nil,
	}

	AlreadyExists = &ErrorResponse{
		StatusCode:       http.StatusConflict, // 409
		Message:          alreadyExistsMessage,
		ValidationErrors: nil,
	}

	InternalServerError = &ErrorResponse{
		StatusCode:       http.StatusInternalServerError, // 500
		Message:          internalServerErrorMessage,
		ValidationErrors: nil,
	}
)
