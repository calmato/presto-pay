// Code generated by MockGen. DO NOT EDIT.
// Source: internal/domain/payment/validation.go

// Package mock_payment is a generated GoMock package.
package mock_payment

import (
	context "context"
	domain "github.com/calmato/presto-pay/api/calc/internal/domain"
	payment "github.com/calmato/presto-pay/api/calc/internal/domain/payment"
	gomock "github.com/golang/mock/gomock"
	reflect "reflect"
)

// MockPaymentDomainValidation is a mock of PaymentDomainValidation interface
type MockPaymentDomainValidation struct {
	ctrl     *gomock.Controller
	recorder *MockPaymentDomainValidationMockRecorder
}

// MockPaymentDomainValidationMockRecorder is the mock recorder for MockPaymentDomainValidation
type MockPaymentDomainValidationMockRecorder struct {
	mock *MockPaymentDomainValidation
}

// NewMockPaymentDomainValidation creates a new mock instance
func NewMockPaymentDomainValidation(ctrl *gomock.Controller) *MockPaymentDomainValidation {
	mock := &MockPaymentDomainValidation{ctrl: ctrl}
	mock.recorder = &MockPaymentDomainValidationMockRecorder{mock}
	return mock
}

// EXPECT returns an object that allows the caller to indicate expected use
func (m *MockPaymentDomainValidation) EXPECT() *MockPaymentDomainValidationMockRecorder {
	return m.recorder
}

// Payment mocks base method
func (m *MockPaymentDomainValidation) Payment(ctx context.Context, p *payment.Payment) []*domain.ValidationError {
	m.ctrl.T.Helper()
	ret := m.ctrl.Call(m, "Payment", ctx, p)
	ret0, _ := ret[0].([]*domain.ValidationError)
	return ret0
}

// Payment indicates an expected call of Payment
func (mr *MockPaymentDomainValidationMockRecorder) Payment(ctx, p interface{}) *gomock.Call {
	mr.mock.ctrl.T.Helper()
	return mr.mock.ctrl.RecordCallWithMethodType(mr.mock, "Payment", reflect.TypeOf((*MockPaymentDomainValidation)(nil).Payment), ctx, p)
}
