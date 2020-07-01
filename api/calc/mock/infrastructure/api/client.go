// Code generated by MockGen. DO NOT EDIT.
// Source: internal/infrastructure/api/client.go

// Package mock_api is a generated GoMock package.
package mock_api

import (
	context "context"
	user "github.com/calmato/presto-pay/api/calc/internal/domain/user"
	gomock "github.com/golang/mock/gomock"
	reflect "reflect"
)

// MockAPIClient is a mock of APIClient interface
type MockAPIClient struct {
	ctrl     *gomock.Controller
	recorder *MockAPIClientMockRecorder
}

// MockAPIClientMockRecorder is the mock recorder for MockAPIClient
type MockAPIClientMockRecorder struct {
	mock *MockAPIClient
}

// NewMockAPIClient creates a new mock instance
func NewMockAPIClient(ctrl *gomock.Controller) *MockAPIClient {
	mock := &MockAPIClient{ctrl: ctrl}
	mock.recorder = &MockAPIClientMockRecorder{mock}
	return mock
}

// EXPECT returns an object that allows the caller to indicate expected use
func (m *MockAPIClient) EXPECT() *MockAPIClientMockRecorder {
	return m.recorder
}

// Authentication mocks base method
func (m *MockAPIClient) Authentication(ctx context.Context) (*user.User, error) {
	m.ctrl.T.Helper()
	ret := m.ctrl.Call(m, "Authentication", ctx)
	ret0, _ := ret[0].(*user.User)
	ret1, _ := ret[1].(error)
	return ret0, ret1
}

// Authentication indicates an expected call of Authentication
func (mr *MockAPIClientMockRecorder) Authentication(ctx interface{}) *gomock.Call {
	mr.mock.ctrl.T.Helper()
	return mr.mock.ctrl.RecordCallWithMethodType(mr.mock, "Authentication", reflect.TypeOf((*MockAPIClient)(nil).Authentication), ctx)
}

// ShowUser mocks base method
func (m *MockAPIClient) ShowUser(ctx context.Context, userID string) (*user.User, error) {
	m.ctrl.T.Helper()
	ret := m.ctrl.Call(m, "ShowUser", ctx, userID)
	ret0, _ := ret[0].(*user.User)
	ret1, _ := ret[1].(error)
	return ret0, ret1
}

// ShowUser indicates an expected call of ShowUser
func (mr *MockAPIClientMockRecorder) ShowUser(ctx, userID interface{}) *gomock.Call {
	mr.mock.ctrl.T.Helper()
	return mr.mock.ctrl.RecordCallWithMethodType(mr.mock, "ShowUser", reflect.TypeOf((*MockAPIClient)(nil).ShowUser), ctx, userID)
}

// UserExists mocks base method
func (m *MockAPIClient) UserExists(ctx context.Context, userID string) (bool, error) {
	m.ctrl.T.Helper()
	ret := m.ctrl.Call(m, "UserExists", ctx, userID)
	ret0, _ := ret[0].(bool)
	ret1, _ := ret[1].(error)
	return ret0, ret1
}

// UserExists indicates an expected call of UserExists
func (mr *MockAPIClientMockRecorder) UserExists(ctx, userID interface{}) *gomock.Call {
	mr.mock.ctrl.T.Helper()
	return mr.mock.ctrl.RecordCallWithMethodType(mr.mock, "UserExists", reflect.TypeOf((*MockAPIClient)(nil).UserExists), ctx, userID)
}

// AddGroup mocks base method
func (m *MockAPIClient) AddGroup(ctx context.Context, userID, groupID string) error {
	m.ctrl.T.Helper()
	ret := m.ctrl.Call(m, "AddGroup", ctx, userID, groupID)
	ret0, _ := ret[0].(error)
	return ret0
}

// AddGroup indicates an expected call of AddGroup
func (mr *MockAPIClientMockRecorder) AddGroup(ctx, userID, groupID interface{}) *gomock.Call {
	mr.mock.ctrl.T.Helper()
	return mr.mock.ctrl.RecordCallWithMethodType(mr.mock, "AddGroup", reflect.TypeOf((*MockAPIClient)(nil).AddGroup), ctx, userID, groupID)
}

// RemoveGroup mocks base method
func (m *MockAPIClient) RemoveGroup(ctx context.Context, userID, groupID string) error {
	m.ctrl.T.Helper()
	ret := m.ctrl.Call(m, "RemoveGroup", ctx, userID, groupID)
	ret0, _ := ret[0].(error)
	return ret0
}

// RemoveGroup indicates an expected call of RemoveGroup
func (mr *MockAPIClientMockRecorder) RemoveGroup(ctx, userID, groupID interface{}) *gomock.Call {
	mr.mock.ctrl.T.Helper()
	return mr.mock.ctrl.RecordCallWithMethodType(mr.mock, "RemoveGroup", reflect.TypeOf((*MockAPIClient)(nil).RemoveGroup), ctx, userID, groupID)
}
