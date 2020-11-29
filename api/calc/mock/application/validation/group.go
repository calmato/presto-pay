// Code generated by MockGen. DO NOT EDIT.
// Source: internal/application/validation/group.go

// Package mock_validation is a generated GoMock package.
package mock_validation

import (
	request "github.com/calmato/presto-pay/api/calc/internal/application/request"
	domain "github.com/calmato/presto-pay/api/calc/internal/domain"
	gomock "github.com/golang/mock/gomock"
	reflect "reflect"
)

// MockGroupRequestValidation is a mock of GroupRequestValidation interface
type MockGroupRequestValidation struct {
	ctrl     *gomock.Controller
	recorder *MockGroupRequestValidationMockRecorder
}

// MockGroupRequestValidationMockRecorder is the mock recorder for MockGroupRequestValidation
type MockGroupRequestValidationMockRecorder struct {
	mock *MockGroupRequestValidation
}

// NewMockGroupRequestValidation creates a new mock instance
func NewMockGroupRequestValidation(ctrl *gomock.Controller) *MockGroupRequestValidation {
	mock := &MockGroupRequestValidation{ctrl: ctrl}
	mock.recorder = &MockGroupRequestValidationMockRecorder{mock}
	return mock
}

// EXPECT returns an object that allows the caller to indicate expected use
func (m *MockGroupRequestValidation) EXPECT() *MockGroupRequestValidationMockRecorder {
	return m.recorder
}

// CreateGroup mocks base method
func (m *MockGroupRequestValidation) CreateGroup(req *request.CreateGroup) []*domain.ValidationError {
	m.ctrl.T.Helper()
	ret := m.ctrl.Call(m, "CreateGroup", req)
	ret0, _ := ret[0].([]*domain.ValidationError)
	return ret0
}

// CreateGroup indicates an expected call of CreateGroup
func (mr *MockGroupRequestValidationMockRecorder) CreateGroup(req interface{}) *gomock.Call {
	mr.mock.ctrl.T.Helper()
	return mr.mock.ctrl.RecordCallWithMethodType(mr.mock, "CreateGroup", reflect.TypeOf((*MockGroupRequestValidation)(nil).CreateGroup), req)
}

// UpdateGroup mocks base method
func (m *MockGroupRequestValidation) UpdateGroup(req *request.UpdateGroup) []*domain.ValidationError {
	m.ctrl.T.Helper()
	ret := m.ctrl.Call(m, "UpdateGroup", req)
	ret0, _ := ret[0].([]*domain.ValidationError)
	return ret0
}

// UpdateGroup indicates an expected call of UpdateGroup
func (mr *MockGroupRequestValidationMockRecorder) UpdateGroup(req interface{}) *gomock.Call {
	mr.mock.ctrl.T.Helper()
	return mr.mock.ctrl.RecordCallWithMethodType(mr.mock, "UpdateGroup", reflect.TypeOf((*MockGroupRequestValidation)(nil).UpdateGroup), req)
}

// AddUsersInGroup mocks base method
func (m *MockGroupRequestValidation) AddUsersInGroup(req *request.AddUsersInGroup) []*domain.ValidationError {
	m.ctrl.T.Helper()
	ret := m.ctrl.Call(m, "AddUsersInGroup", req)
	ret0, _ := ret[0].([]*domain.ValidationError)
	return ret0
}

// AddUsersInGroup indicates an expected call of AddUsersInGroup
func (mr *MockGroupRequestValidationMockRecorder) AddUsersInGroup(req interface{}) *gomock.Call {
	mr.mock.ctrl.T.Helper()
	return mr.mock.ctrl.RecordCallWithMethodType(mr.mock, "AddUsersInGroup", reflect.TypeOf((*MockGroupRequestValidation)(nil).AddUsersInGroup), req)
}

// AddUnauthorizedUsersInGroup mocks base method
func (m *MockGroupRequestValidation) AddUnauthorizedUsersInGroup(req *request.AddUnauthorizedUsersInGroup) []*domain.ValidationError {
	m.ctrl.T.Helper()
	ret := m.ctrl.Call(m, "AddUnauthorizedUsersInGroup", req)
	ret0, _ := ret[0].([]*domain.ValidationError)
	return ret0
}

// AddUnauthorizedUsersInGroup indicates an expected call of AddUnauthorizedUsersInGroup
func (mr *MockGroupRequestValidationMockRecorder) AddUnauthorizedUsersInGroup(req interface{}) *gomock.Call {
	mr.mock.ctrl.T.Helper()
	return mr.mock.ctrl.RecordCallWithMethodType(mr.mock, "AddUnauthorizedUsersInGroup", reflect.TypeOf((*MockGroupRequestValidation)(nil).AddUnauthorizedUsersInGroup), req)
}

// RemoveUsersInGroup mocks base method
func (m *MockGroupRequestValidation) RemoveUsersInGroup(req *request.RemoveUsersInGroup) []*domain.ValidationError {
	m.ctrl.T.Helper()
	ret := m.ctrl.Call(m, "RemoveUsersInGroup", req)
	ret0, _ := ret[0].([]*domain.ValidationError)
	return ret0
}

// RemoveUsersInGroup indicates an expected call of RemoveUsersInGroup
func (mr *MockGroupRequestValidationMockRecorder) RemoveUsersInGroup(req interface{}) *gomock.Call {
	mr.mock.ctrl.T.Helper()
	return mr.mock.ctrl.RecordCallWithMethodType(mr.mock, "RemoveUsersInGroup", reflect.TypeOf((*MockGroupRequestValidation)(nil).RemoveUsersInGroup), req)
}
