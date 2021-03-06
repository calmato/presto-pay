// Code generated by MockGen. DO NOT EDIT.
// Source: internal/application/validation/payment.go

// Package mock_validation is a generated GoMock package.
package mock_validation

import (
	request "github.com/calmato/presto-pay/api/calc/internal/application/request"
	domain "github.com/calmato/presto-pay/api/calc/internal/domain"
	gomock "github.com/golang/mock/gomock"
	reflect "reflect"
)

// MockPaymentRequestValidation is a mock of PaymentRequestValidation interface
type MockPaymentRequestValidation struct {
	ctrl     *gomock.Controller
	recorder *MockPaymentRequestValidationMockRecorder
}

// MockPaymentRequestValidationMockRecorder is the mock recorder for MockPaymentRequestValidation
type MockPaymentRequestValidationMockRecorder struct {
	mock *MockPaymentRequestValidation
}

// NewMockPaymentRequestValidation creates a new mock instance
func NewMockPaymentRequestValidation(ctrl *gomock.Controller) *MockPaymentRequestValidation {
	mock := &MockPaymentRequestValidation{ctrl: ctrl}
	mock.recorder = &MockPaymentRequestValidationMockRecorder{mock}
	return mock
}

// EXPECT returns an object that allows the caller to indicate expected use
func (m *MockPaymentRequestValidation) EXPECT() *MockPaymentRequestValidationMockRecorder {
	return m.recorder
}

// CreatePayment mocks base method
func (m *MockPaymentRequestValidation) CreatePayment(req *request.CreatePayment) []*domain.ValidationError {
	m.ctrl.T.Helper()
	ret := m.ctrl.Call(m, "CreatePayment", req)
	ret0, _ := ret[0].([]*domain.ValidationError)
	return ret0
}

// CreatePayment indicates an expected call of CreatePayment
func (mr *MockPaymentRequestValidationMockRecorder) CreatePayment(req interface{}) *gomock.Call {
	mr.mock.ctrl.T.Helper()
	return mr.mock.ctrl.RecordCallWithMethodType(mr.mock, "CreatePayment", reflect.TypeOf((*MockPaymentRequestValidation)(nil).CreatePayment), req)
}

// UpdatePayment mocks base method
func (m *MockPaymentRequestValidation) UpdatePayment(req *request.UpdatePayment) []*domain.ValidationError {
	m.ctrl.T.Helper()
	ret := m.ctrl.Call(m, "UpdatePayment", req)
	ret0, _ := ret[0].([]*domain.ValidationError)
	return ret0
}

// UpdatePayment indicates an expected call of UpdatePayment
func (mr *MockPaymentRequestValidationMockRecorder) UpdatePayment(req interface{}) *gomock.Call {
	mr.mock.ctrl.T.Helper()
	return mr.mock.ctrl.RecordCallWithMethodType(mr.mock, "UpdatePayment", reflect.TypeOf((*MockPaymentRequestValidation)(nil).UpdatePayment), req)
}

// UpdatePayerInPayment mocks base method
func (m *MockPaymentRequestValidation) UpdatePayerInPayment(req *request.UpdatePayerInPayment) []*domain.ValidationError {
	m.ctrl.T.Helper()
	ret := m.ctrl.Call(m, "UpdatePayerInPayment", req)
	ret0, _ := ret[0].([]*domain.ValidationError)
	return ret0
}

// UpdatePayerInPayment indicates an expected call of UpdatePayerInPayment
func (mr *MockPaymentRequestValidationMockRecorder) UpdatePayerInPayment(req interface{}) *gomock.Call {
	mr.mock.ctrl.T.Helper()
	return mr.mock.ctrl.RecordCallWithMethodType(mr.mock, "UpdatePayerInPayment", reflect.TypeOf((*MockPaymentRequestValidation)(nil).UpdatePayerInPayment), req)
}
