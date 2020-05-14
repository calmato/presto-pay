package handler

import (
	"github.com/calmato/presto-pay/api/user/internal/application/response"
	"github.com/calmato/presto-pay/api/user/internal/domain"
	"github.com/gin-gonic/gin"
	log "github.com/sirupsen/logrus"
)

// outputLevel - ログの出力レベル
type outputLevel int

const (
	debugLevel outputLevel = iota
	infoLevel
	warnLevel
	errorLevel
)

// ErrorHandling - エラーレスポンスを返す
func ErrorHandling(ctx *gin.Context, err error) {
	res := getErrorResponse(err)

	ctx.JSON(res.StatusCode, res)
	ctx.Abort()
}

// getErrorResponse - エラー用のレスポンスを返す
func getErrorResponse(err error) *response.ErrorResponse {
	res := response.ErrorResponse{}
	level := infoLevel
	message := ""

	switch getErrorCode(err) {
	// 400
	case domain.InvalidDomainValidation:
		res = *response.BadRequest
		res.ValidationErrors = getValidationErrorsInErrorReponse(err)
		message = "Invalid domain validation"
	case domain.InvalidRequestValidation:
		res = *response.BadRequest
		res.ValidationErrors = getValidationErrorsInErrorReponse(err)
		message = "Invalid request validation"
	case domain.UnableParseJSON:
		res = *response.BadRequest
		message = "Unable parse json"
	case domain.UnableConvertStringToByte64:
		res = *response.BadRequest
		message = "Unable convert from string to byte64"
	case domain.NotEqualRequestWithDatastore:
		res = *response.BadRequest
		message = "Invalid request validation"
	// 401
	case domain.Unauthorized:
		res = *response.Unauthorized
		message = "Unauthorized"
	// 403
	case domain.Forbidden:
		res = *response.Forbidden
		message = "Forbidden"
	// 404
	case domain.NotFound:
		res = *response.NotFound
		message = "Not found"
	// 409
	case domain.AlreadyExistsInDatastore:
		res = *response.AlreadyExists
		res.ValidationErrors = getValidationErrorsInErrorReponse(err)
		message = "Already exists request"
	// 500
	case domain.ErrorInDatastore:
		res = *response.InternalServerError
		level = warnLevel
		message = "Error in datastore"
	case domain.ErrorInStorage:
		res = *response.InternalServerError
		level = warnLevel
		message = "Error in storage"
	default:
		res = *response.InternalServerError
		level = errorLevel
		message = "Internal server error"
	}

	res.ErrorCode = getErrorCode(err)
	logging(level, message, err, &res)

	return &res
}

func logging(level outputLevel, message string, err error, res *response.ErrorResponse) {
	fields := log.Fields{
		"message":   message,
		"errorCode": res.ErrorCode,
	}

	if len(res.ValidationErrors) > 0 {
		fields["validationErrors"] = res.ValidationErrors
	}

	switch level {
	case debugLevel:
		log.WithFields(fields).Debug(getError(err))
	case infoLevel:
		log.WithFields(fields).Info(getError(err))
	case warnLevel:
		log.WithFields(fields).Info(getError(err))
	default:
		log.WithFields(fields).Error(getError(err))
	}
}

func getError(err error) string {
	if e, ok := err.(domain.CustomError); ok {
		return e.Error()
	}

	return ""
}

func getErrorCode(err error) domain.ErrorCode {
	if e, ok := err.(domain.CustomError); ok {
		return e.Code()
	}

	return domain.Unknown
}

func getValidationErrorsInErrorReponse(err error) []*response.ValidationError {
	if e, ok := err.(domain.CustomError); ok {
		ves := make([]*response.ValidationError, len(e.Validations()))
		for i, ve := range e.Validations() {
			ves[i] = &response.ValidationError{
				Field:   ve.Field,
				Message: ve.Message,
			}
		}

		return ves
	}

	return []*response.ValidationError{}
}
