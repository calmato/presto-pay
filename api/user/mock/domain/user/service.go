// Code generated by MockGen. DO NOT EDIT.
// Source: internal/domain/user/service.go

// Package mock_user is a generated GoMock package.
package mock_user

import (
	context "context"
	user "github.com/calmato/presto-pay/api/user/internal/domain/user"
	gomock "github.com/golang/mock/gomock"
	reflect "reflect"
)

// MockUserService is a mock of UserService interface
type MockUserService struct {
	ctrl     *gomock.Controller
	recorder *MockUserServiceMockRecorder
}

// MockUserServiceMockRecorder is the mock recorder for MockUserService
type MockUserServiceMockRecorder struct {
	mock *MockUserService
}

// NewMockUserService creates a new mock instance
func NewMockUserService(ctrl *gomock.Controller) *MockUserService {
	mock := &MockUserService{ctrl: ctrl}
	mock.recorder = &MockUserServiceMockRecorder{mock}
	return mock
}

// EXPECT returns an object that allows the caller to indicate expected use
func (m *MockUserService) EXPECT() *MockUserServiceMockRecorder {
	return m.recorder
}

// Authentication mocks base method
func (m *MockUserService) Authentication(ctx context.Context) (*user.User, error) {
	m.ctrl.T.Helper()
	ret := m.ctrl.Call(m, "Authentication", ctx)
	ret0, _ := ret[0].(*user.User)
	ret1, _ := ret[1].(error)
	return ret0, ret1
}

// Authentication indicates an expected call of Authentication
func (mr *MockUserServiceMockRecorder) Authentication(ctx interface{}) *gomock.Call {
	mr.mock.ctrl.T.Helper()
	return mr.mock.ctrl.RecordCallWithMethodType(mr.mock, "Authentication", reflect.TypeOf((*MockUserService)(nil).Authentication), ctx)
}

// Create mocks base method
func (m *MockUserService) Create(ctx context.Context, u *user.User) (*user.User, error) {
	m.ctrl.T.Helper()
	ret := m.ctrl.Call(m, "Create", ctx, u)
	ret0, _ := ret[0].(*user.User)
	ret1, _ := ret[1].(error)
	return ret0, ret1
}

// Create indicates an expected call of Create
func (mr *MockUserServiceMockRecorder) Create(ctx, u interface{}) *gomock.Call {
	mr.mock.ctrl.T.Helper()
	return mr.mock.ctrl.RecordCallWithMethodType(mr.mock, "Create", reflect.TypeOf((*MockUserService)(nil).Create), ctx, u)
}

// Update mocks base method
func (m *MockUserService) Update(ctx context.Context, u *user.User) (*user.User, error) {
	m.ctrl.T.Helper()
	ret := m.ctrl.Call(m, "Update", ctx, u)
	ret0, _ := ret[0].(*user.User)
	ret1, _ := ret[1].(error)
	return ret0, ret1
}

// Update indicates an expected call of Update
func (mr *MockUserServiceMockRecorder) Update(ctx, u interface{}) *gomock.Call {
	mr.mock.ctrl.T.Helper()
	return mr.mock.ctrl.RecordCallWithMethodType(mr.mock, "Update", reflect.TypeOf((*MockUserService)(nil).Update), ctx, u)
}

// UpdatePassword mocks base method
func (m *MockUserService) UpdatePassword(ctx context.Context, uid, password string) error {
	m.ctrl.T.Helper()
	ret := m.ctrl.Call(m, "UpdatePassword", ctx, uid, password)
	ret0, _ := ret[0].(error)
	return ret0
}

// UpdatePassword indicates an expected call of UpdatePassword
func (mr *MockUserServiceMockRecorder) UpdatePassword(ctx, uid, password interface{}) *gomock.Call {
	mr.mock.ctrl.T.Helper()
	return mr.mock.ctrl.RecordCallWithMethodType(mr.mock, "UpdatePassword", reflect.TypeOf((*MockUserService)(nil).UpdatePassword), ctx, uid, password)
}

// UploadThumbnail mocks base method
func (m *MockUserService) UploadThumbnail(ctx context.Context, data []byte) (string, error) {
	m.ctrl.T.Helper()
	ret := m.ctrl.Call(m, "UploadThumbnail", ctx, data)
	ret0, _ := ret[0].(string)
	ret1, _ := ret[1].(error)
	return ret0, ret1
}

// UploadThumbnail indicates an expected call of UploadThumbnail
func (mr *MockUserServiceMockRecorder) UploadThumbnail(ctx, data interface{}) *gomock.Call {
	mr.mock.ctrl.T.Helper()
	return mr.mock.ctrl.RecordCallWithMethodType(mr.mock, "UploadThumbnail", reflect.TypeOf((*MockUserService)(nil).UploadThumbnail), ctx, data)
}

// UniqueCheckEmail mocks base method
func (m *MockUserService) UniqueCheckEmail(ctx context.Context, auth *user.User, email string) bool {
	m.ctrl.T.Helper()
	ret := m.ctrl.Call(m, "UniqueCheckEmail", ctx, auth, email)
	ret0, _ := ret[0].(bool)
	return ret0
}

// UniqueCheckEmail indicates an expected call of UniqueCheckEmail
func (mr *MockUserServiceMockRecorder) UniqueCheckEmail(ctx, auth, email interface{}) *gomock.Call {
	mr.mock.ctrl.T.Helper()
	return mr.mock.ctrl.RecordCallWithMethodType(mr.mock, "UniqueCheckEmail", reflect.TypeOf((*MockUserService)(nil).UniqueCheckEmail), ctx, auth, email)
}

// UniqueCheckUsername mocks base method
func (m *MockUserService) UniqueCheckUsername(ctx context.Context, auth *user.User, username string) bool {
	m.ctrl.T.Helper()
	ret := m.ctrl.Call(m, "UniqueCheckUsername", ctx, auth, username)
	ret0, _ := ret[0].(bool)
	return ret0
}

// UniqueCheckUsername indicates an expected call of UniqueCheckUsername
func (mr *MockUserServiceMockRecorder) UniqueCheckUsername(ctx, auth, username interface{}) *gomock.Call {
	mr.mock.ctrl.T.Helper()
	return mr.mock.ctrl.RecordCallWithMethodType(mr.mock, "UniqueCheckUsername", reflect.TypeOf((*MockUserService)(nil).UniqueCheckUsername), ctx, auth, username)
}

// GroupIDExists mocks base method
func (m *MockUserService) GroupIDExists(ctx context.Context, userID, groupID string) (bool, error) {
	m.ctrl.T.Helper()
	ret := m.ctrl.Call(m, "GroupIDExists", ctx, userID, groupID)
	ret0, _ := ret[0].(bool)
	ret1, _ := ret[1].(error)
	return ret0, ret1
}

// GroupIDExists indicates an expected call of GroupIDExists
func (mr *MockUserServiceMockRecorder) GroupIDExists(ctx, userID, groupID interface{}) *gomock.Call {
	mr.mock.ctrl.T.Helper()
	return mr.mock.ctrl.RecordCallWithMethodType(mr.mock, "GroupIDExists", reflect.TypeOf((*MockUserService)(nil).GroupIDExists), ctx, userID, groupID)
}
