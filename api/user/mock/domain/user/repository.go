// Code generated by MockGen. DO NOT EDIT.
// Source: internal/domain/user/repository.go

// Package mock_user is a generated GoMock package.
package mock_user

import (
	context "context"
	user "github.com/calmato/presto-pay/api/user/internal/domain/user"
	gomock "github.com/golang/mock/gomock"
	reflect "reflect"
)

// MockUserRepository is a mock of UserRepository interface
type MockUserRepository struct {
	ctrl     *gomock.Controller
	recorder *MockUserRepositoryMockRecorder
}

// MockUserRepositoryMockRecorder is the mock recorder for MockUserRepository
type MockUserRepositoryMockRecorder struct {
	mock *MockUserRepository
}

// NewMockUserRepository creates a new mock instance
func NewMockUserRepository(ctrl *gomock.Controller) *MockUserRepository {
	mock := &MockUserRepository{ctrl: ctrl}
	mock.recorder = &MockUserRepositoryMockRecorder{mock}
	return mock
}

// EXPECT returns an object that allows the caller to indicate expected use
func (m *MockUserRepository) EXPECT() *MockUserRepositoryMockRecorder {
	return m.recorder
}

// Authentication mocks base method
func (m *MockUserRepository) Authentication(ctx context.Context) (*user.User, error) {
	m.ctrl.T.Helper()
	ret := m.ctrl.Call(m, "Authentication", ctx)
	ret0, _ := ret[0].(*user.User)
	ret1, _ := ret[1].(error)
	return ret0, ret1
}

// Authentication indicates an expected call of Authentication
func (mr *MockUserRepositoryMockRecorder) Authentication(ctx interface{}) *gomock.Call {
	mr.mock.ctrl.T.Helper()
	return mr.mock.ctrl.RecordCallWithMethodType(mr.mock, "Authentication", reflect.TypeOf((*MockUserRepository)(nil).Authentication), ctx)
}

// Create mocks base method
func (m *MockUserRepository) Create(ctx context.Context, u *user.User) error {
	m.ctrl.T.Helper()
	ret := m.ctrl.Call(m, "Create", ctx, u)
	ret0, _ := ret[0].(error)
	return ret0
}

// Create indicates an expected call of Create
func (mr *MockUserRepositoryMockRecorder) Create(ctx, u interface{}) *gomock.Call {
	mr.mock.ctrl.T.Helper()
	return mr.mock.ctrl.RecordCallWithMethodType(mr.mock, "Create", reflect.TypeOf((*MockUserRepository)(nil).Create), ctx, u)
}

// GetUIDByEmail mocks base method
func (m *MockUserRepository) GetUIDByEmail(ctx context.Context, email string) (string, error) {
	m.ctrl.T.Helper()
	ret := m.ctrl.Call(m, "GetUIDByEmail", ctx, email)
	ret0, _ := ret[0].(string)
	ret1, _ := ret[1].(error)
	return ret0, ret1
}

// GetUIDByEmail indicates an expected call of GetUIDByEmail
func (mr *MockUserRepositoryMockRecorder) GetUIDByEmail(ctx, email interface{}) *gomock.Call {
	mr.mock.ctrl.T.Helper()
	return mr.mock.ctrl.RecordCallWithMethodType(mr.mock, "GetUIDByEmail", reflect.TypeOf((*MockUserRepository)(nil).GetUIDByEmail), ctx, email)
}