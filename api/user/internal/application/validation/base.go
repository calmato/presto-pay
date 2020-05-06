package validation

import (
	"fmt"
	"reflect"
	"regexp"

	"github.com/calmato/presto-pay/api/user/internal/domain"
	"github.com/go-playground/validator/v10"
)

// RequestValidator - リクエストバリデーションインターフェース
type RequestValidator interface {
	Run(i interface{}) []*domain.ValidationError
}

type requestValidator struct {
	validate validator.Validate
}

// NewRequestValidator - Validatorの生成
func NewRequestValidator() RequestValidator {
	validate := validator.New()

	if err := validate.RegisterValidation("password", passwordCheck); err != nil {
		return nil
	}

	return &requestValidator{
		validate: *validate,
	}
}

const (
	passwordString = "^[a-zA-Z0-9_!@#$_%^&*.?()-=+]*$"
)

var (
	passwordRegex = regexp.MustCompile(passwordString)
)

// Run - バリデーションの実行
func (rv *requestValidator) Run(i interface{}) []*domain.ValidationError {
	err := rv.validate.Struct(i)
	if err == nil {
		return make([]*domain.ValidationError, 0)
	}

	errors := err.(validator.ValidationErrors)
	validationErrors := make([]*domain.ValidationError, len(errors))

	rt := reflect.ValueOf(i).Elem().Type()

	for i, v := range errors {
		errorField, _ := rt.FieldByName(v.Field())
		errorFieldName := errorField.Tag.Get("label")
		errorMessage := ""

		switch v.Tag() {
		case domain.EqFieldTag:
			eqField, _ := rt.FieldByName(v.Param())
			errorMessage = validationMessage(v.Tag(), eqField.Tag.Get("label"))
		default:
			errorMessage = validationMessage(v.Tag(), v.Param())
		}

		validationErrors[i] = &domain.ValidationError{
			Field:   errorFieldName,
			Message: errorMessage,
		}
	}

	return validationErrors
}

func passwordCheck(fl validator.FieldLevel) bool {
	return passwordRegex.MatchString(fl.Field().String())
}

func validationMessage(tag string, options ...string) string {
	switch tag {
	case domain.RequiredTag:
		return domain.RequiredMessage
	case domain.EqFieldTag:
		return fmt.Sprintf(domain.EqFieldMessage, options[0])
	case domain.MinTag:
		return fmt.Sprintf(domain.MinMessage, options[0])
	case domain.MaxTag:
		return fmt.Sprintf(domain.MaxMessage, options[0])
	case domain.UniqueTag:
		return domain.UniqueMessage
	default:
		return ""
	}
}
