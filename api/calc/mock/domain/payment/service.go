// Code generated by MockGen. DO NOT EDIT.
// Source: internal/domain/payment/service.go

// Package mock_payment is a generated GoMock package.
package mock_payment

import (
	context "context"
	payment "github.com/calmato/presto-pay/api/calc/internal/domain/payment"
	gomock "github.com/golang/mock/gomock"
	reflect "reflect"
)

// MockPaymentService is a mock of PaymentService interface
type MockPaymentService struct {
	ctrl     *gomock.Controller
	recorder *MockPaymentServiceMockRecorder
}

// MockPaymentServiceMockRecorder is the mock recorder for MockPaymentService
type MockPaymentServiceMockRecorder struct {
	mock *MockPaymentService
}

// NewMockPaymentService creates a new mock instance
func NewMockPaymentService(ctrl *gomock.Controller) *MockPaymentService {
	mock := &MockPaymentService{ctrl: ctrl}
	mock.recorder = &MockPaymentServiceMockRecorder{mock}
	return mock
}

// EXPECT returns an object that allows the caller to indicate expected use
func (m *MockPaymentService) EXPECT() *MockPaymentServiceMockRecorder {
	return m.recorder
}

// Index mocks base method
func (m *MockPaymentService) Index(ctx context.Context, groupID, startAt string) ([]*payment.Payment, error) {
	m.ctrl.T.Helper()
	ret := m.ctrl.Call(m, "Index", ctx, groupID, startAt)
	ret0, _ := ret[0].([]*payment.Payment)
	ret1, _ := ret[1].(error)
	return ret0, ret1
}

// Index indicates an expected call of Index
func (mr *MockPaymentServiceMockRecorder) Index(ctx, groupID, startAt interface{}) *gomock.Call {
	mr.mock.ctrl.T.Helper()
	return mr.mock.ctrl.RecordCallWithMethodType(mr.mock, "Index", reflect.TypeOf((*MockPaymentService)(nil).Index), ctx, groupID, startAt)
}

// IndexByIsCompleted mocks base method
func (m *MockPaymentService) IndexByIsCompleted(ctx context.Context, groupID string, isCompleted bool) ([]*payment.Payment, error) {
	m.ctrl.T.Helper()
	ret := m.ctrl.Call(m, "IndexByIsCompleted", ctx, groupID, isCompleted)
	ret0, _ := ret[0].([]*payment.Payment)
	ret1, _ := ret[1].(error)
	return ret0, ret1
}

// IndexByIsCompleted indicates an expected call of IndexByIsCompleted
func (mr *MockPaymentServiceMockRecorder) IndexByIsCompleted(ctx, groupID, isCompleted interface{}) *gomock.Call {
	mr.mock.ctrl.T.Helper()
	return mr.mock.ctrl.RecordCallWithMethodType(mr.mock, "IndexByIsCompleted", reflect.TypeOf((*MockPaymentService)(nil).IndexByIsCompleted), ctx, groupID, isCompleted)
}

// Show mocks base method
func (m *MockPaymentService) Show(ctx context.Context, groupID, paymentID string) (*payment.Payment, error) {
	m.ctrl.T.Helper()
	ret := m.ctrl.Call(m, "Show", ctx, groupID, paymentID)
	ret0, _ := ret[0].(*payment.Payment)
	ret1, _ := ret[1].(error)
	return ret0, ret1
}

// Show indicates an expected call of Show
func (mr *MockPaymentServiceMockRecorder) Show(ctx, groupID, paymentID interface{}) *gomock.Call {
	mr.mock.ctrl.T.Helper()
	return mr.mock.ctrl.RecordCallWithMethodType(mr.mock, "Show", reflect.TypeOf((*MockPaymentService)(nil).Show), ctx, groupID, paymentID)
}

// Create mocks base method
func (m *MockPaymentService) Create(ctx context.Context, p *payment.Payment, groupID string) (*payment.Payment, error) {
	m.ctrl.T.Helper()
	ret := m.ctrl.Call(m, "Create", ctx, p, groupID)
	ret0, _ := ret[0].(*payment.Payment)
	ret1, _ := ret[1].(error)
	return ret0, ret1
}

// Create indicates an expected call of Create
func (mr *MockPaymentServiceMockRecorder) Create(ctx, p, groupID interface{}) *gomock.Call {
	mr.mock.ctrl.T.Helper()
	return mr.mock.ctrl.RecordCallWithMethodType(mr.mock, "Create", reflect.TypeOf((*MockPaymentService)(nil).Create), ctx, p, groupID)
}

// Update mocks base method
func (m *MockPaymentService) Update(ctx context.Context, p *payment.Payment, groupID string) (*payment.Payment, error) {
	m.ctrl.T.Helper()
	ret := m.ctrl.Call(m, "Update", ctx, p, groupID)
	ret0, _ := ret[0].(*payment.Payment)
	ret1, _ := ret[1].(error)
	return ret0, ret1
}

// Update indicates an expected call of Update
func (mr *MockPaymentServiceMockRecorder) Update(ctx, p, groupID interface{}) *gomock.Call {
	mr.mock.ctrl.T.Helper()
	return mr.mock.ctrl.RecordCallWithMethodType(mr.mock, "Update", reflect.TypeOf((*MockPaymentService)(nil).Update), ctx, p, groupID)
}

// UpdatePayer mocks base method
func (m *MockPaymentService) UpdatePayer(ctx context.Context, p *payment.Payment, groupID, payerID string, isPaid bool) (*payment.Payment, error) {
	m.ctrl.T.Helper()
	ret := m.ctrl.Call(m, "UpdatePayer", ctx, p, groupID, payerID, isPaid)
	ret0, _ := ret[0].(*payment.Payment)
	ret1, _ := ret[1].(error)
	return ret0, ret1
}

// UpdatePayer indicates an expected call of UpdatePayer
func (mr *MockPaymentServiceMockRecorder) UpdatePayer(ctx, p, groupID, payerID, isPaid interface{}) *gomock.Call {
	mr.mock.ctrl.T.Helper()
	return mr.mock.ctrl.RecordCallWithMethodType(mr.mock, "UpdatePayer", reflect.TypeOf((*MockPaymentService)(nil).UpdatePayer), ctx, p, groupID, payerID, isPaid)
}

// Destroy mocks base method
func (m *MockPaymentService) Destroy(ctx context.Context, groupID, paymentID string) error {
	m.ctrl.T.Helper()
	ret := m.ctrl.Call(m, "Destroy", ctx, groupID, paymentID)
	ret0, _ := ret[0].(error)
	return ret0
}

// Destroy indicates an expected call of Destroy
func (mr *MockPaymentServiceMockRecorder) Destroy(ctx, groupID, paymentID interface{}) *gomock.Call {
	mr.mock.ctrl.T.Helper()
	return mr.mock.ctrl.RecordCallWithMethodType(mr.mock, "Destroy", reflect.TypeOf((*MockPaymentService)(nil).Destroy), ctx, groupID, paymentID)
}

// UploadImage mocks base method
func (m *MockPaymentService) UploadImage(ctx context.Context, data []byte) (string, error) {
	m.ctrl.T.Helper()
	ret := m.ctrl.Call(m, "UploadImage", ctx, data)
	ret0, _ := ret[0].(string)
	ret1, _ := ret[1].(error)
	return ret0, ret1
}

// UploadImage indicates an expected call of UploadImage
func (mr *MockPaymentServiceMockRecorder) UploadImage(ctx, data interface{}) *gomock.Call {
	mr.mock.ctrl.T.Helper()
	return mr.mock.ctrl.RecordCallWithMethodType(mr.mock, "UploadImage", reflect.TypeOf((*MockPaymentService)(nil).UploadImage), ctx, data)
}
