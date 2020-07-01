// Code generated by MockGen. DO NOT EDIT.
// Source: internal/application/validation/user.go

// Package mock_validation is a generated GoMock package.
package mock_validation

import (
	request "github.com/calmato/presto-pay/api/user/internal/application/request"
	domain "github.com/calmato/presto-pay/api/user/internal/domain"
	gomock "github.com/golang/mock/gomock"
	reflect "reflect"
)

// MockUserRequestValidation is a mock of UserRequestValidation interface
type MockUserRequestValidation struct {
	ctrl     *gomock.Controller
	recorder *MockUserRequestValidationMockRecorder
}

// MockUserRequestValidationMockRecorder is the mock recorder for MockUserRequestValidation
type MockUserRequestValidationMockRecorder struct {
	mock *MockUserRequestValidation
}

// NewMockUserRequestValidation creates a new mock instance
func NewMockUserRequestValidation(ctrl *gomock.Controller) *MockUserRequestValidation {
	mock := &MockUserRequestValidation{ctrl: ctrl}
	mock.recorder = &MockUserRequestValidationMockRecorder{mock}
	return mock
}

// EXPECT returns an object that allows the caller to indicate expected use
func (m *MockUserRequestValidation) EXPECT() *MockUserRequestValidationMockRecorder {
	return m.recorder
}

// IndexByUsername mocks base method
func (m *MockUserRequestValidation) IndexByUsername(req *request.IndexByUsername) []*domain.ValidationError {
	m.ctrl.T.Helper()
	ret := m.ctrl.Call(m, "IndexByUsername", req)
	ret0, _ := ret[0].([]*domain.ValidationError)
	return ret0
}

// IndexByUsername indicates an expected call of IndexByUsername
func (mr *MockUserRequestValidationMockRecorder) IndexByUsername(req interface{}) *gomock.Call {
	mr.mock.ctrl.T.Helper()
	return mr.mock.ctrl.RecordCallWithMethodType(mr.mock, "IndexByUsername", reflect.TypeOf((*MockUserRequestValidation)(nil).IndexByUsername), req)
}

// CreateUser mocks base method
func (m *MockUserRequestValidation) CreateUser(req *request.CreateUser) []*domain.ValidationError {
	m.ctrl.T.Helper()
	ret := m.ctrl.Call(m, "CreateUser", req)
	ret0, _ := ret[0].([]*domain.ValidationError)
	return ret0
}

// CreateUser indicates an expected call of CreateUser
func (mr *MockUserRequestValidationMockRecorder) CreateUser(req interface{}) *gomock.Call {
	mr.mock.ctrl.T.Helper()
	return mr.mock.ctrl.RecordCallWithMethodType(mr.mock, "CreateUser", reflect.TypeOf((*MockUserRequestValidation)(nil).CreateUser), req)
}

// UpdateProfile mocks base method
func (m *MockUserRequestValidation) UpdateProfile(req *request.UpdateProfile) []*domain.ValidationError {
	m.ctrl.T.Helper()
	ret := m.ctrl.Call(m, "UpdateProfile", req)
	ret0, _ := ret[0].([]*domain.ValidationError)
	return ret0
}

// UpdateProfile indicates an expected call of UpdateProfile
func (mr *MockUserRequestValidationMockRecorder) UpdateProfile(req interface{}) *gomock.Call {
	mr.mock.ctrl.T.Helper()
	return mr.mock.ctrl.RecordCallWithMethodType(mr.mock, "UpdateProfile", reflect.TypeOf((*MockUserRequestValidation)(nil).UpdateProfile), req)
}

// UpdatePassword mocks base method
func (m *MockUserRequestValidation) UpdatePassword(req *request.UpdateUserPassword) []*domain.ValidationError {
	m.ctrl.T.Helper()
	ret := m.ctrl.Call(m, "UpdatePassword", req)
	ret0, _ := ret[0].([]*domain.ValidationError)
	return ret0
}

// UpdatePassword indicates an expected call of UpdatePassword
func (mr *MockUserRequestValidationMockRecorder) UpdatePassword(req interface{}) *gomock.Call {
	mr.mock.ctrl.T.Helper()
	return mr.mock.ctrl.RecordCallWithMethodType(mr.mock, "UpdatePassword", reflect.TypeOf((*MockUserRequestValidation)(nil).UpdatePassword), req)
}

// AddFriend mocks base method
func (m *MockUserRequestValidation) AddFriend(req *request.AddFriend) []*domain.ValidationError {
	m.ctrl.T.Helper()
	ret := m.ctrl.Call(m, "AddFriend", req)
	ret0, _ := ret[0].([]*domain.ValidationError)
	return ret0
}

// AddFriend indicates an expected call of AddFriend
func (mr *MockUserRequestValidationMockRecorder) AddFriend(req interface{}) *gomock.Call {
	mr.mock.ctrl.T.Helper()
	return mr.mock.ctrl.RecordCallWithMethodType(mr.mock, "AddFriend", reflect.TypeOf((*MockUserRequestValidation)(nil).AddFriend), req)
}

// UniqueCheckEmail mocks base method
func (m *MockUserRequestValidation) UniqueCheckEmail(req *request.UniqueCheckUserEmail) []*domain.ValidationError {
	m.ctrl.T.Helper()
	ret := m.ctrl.Call(m, "UniqueCheckEmail", req)
	ret0, _ := ret[0].([]*domain.ValidationError)
	return ret0
}

// UniqueCheckEmail indicates an expected call of UniqueCheckEmail
func (mr *MockUserRequestValidationMockRecorder) UniqueCheckEmail(req interface{}) *gomock.Call {
	mr.mock.ctrl.T.Helper()
	return mr.mock.ctrl.RecordCallWithMethodType(mr.mock, "UniqueCheckEmail", reflect.TypeOf((*MockUserRequestValidation)(nil).UniqueCheckEmail), req)
}

// UniqueCheckUsername mocks base method
func (m *MockUserRequestValidation) UniqueCheckUsername(req *request.UniqueCheckUserUsername) []*domain.ValidationError {
	m.ctrl.T.Helper()
	ret := m.ctrl.Call(m, "UniqueCheckUsername", req)
	ret0, _ := ret[0].([]*domain.ValidationError)
	return ret0
}

// UniqueCheckUsername indicates an expected call of UniqueCheckUsername
func (mr *MockUserRequestValidationMockRecorder) UniqueCheckUsername(req interface{}) *gomock.Call {
	mr.mock.ctrl.T.Helper()
	return mr.mock.ctrl.RecordCallWithMethodType(mr.mock, "UniqueCheckUsername", reflect.TypeOf((*MockUserRequestValidation)(nil).UniqueCheckUsername), req)
}
