// Code generated by MockGen. DO NOT EDIT.
// Source: internal/domain/group/service.go

// Package mock_group is a generated GoMock package.
package mock_group

import (
	context "context"
	group "github.com/calmato/presto-pay/api/calc/internal/domain/group"
	user "github.com/calmato/presto-pay/api/calc/internal/domain/user"
	gomock "github.com/golang/mock/gomock"
	reflect "reflect"
)

// MockGroupService is a mock of GroupService interface
type MockGroupService struct {
	ctrl     *gomock.Controller
	recorder *MockGroupServiceMockRecorder
}

// MockGroupServiceMockRecorder is the mock recorder for MockGroupService
type MockGroupServiceMockRecorder struct {
	mock *MockGroupService
}

// NewMockGroupService creates a new mock instance
func NewMockGroupService(ctrl *gomock.Controller) *MockGroupService {
	mock := &MockGroupService{ctrl: ctrl}
	mock.recorder = &MockGroupServiceMockRecorder{mock}
	return mock
}

// EXPECT returns an object that allows the caller to indicate expected use
func (m *MockGroupService) EXPECT() *MockGroupServiceMockRecorder {
	return m.recorder
}

// Index mocks base method
func (m *MockGroupService) Index(ctx context.Context, u *user.User) ([]*group.Group, []*group.Group, error) {
	m.ctrl.T.Helper()
	ret := m.ctrl.Call(m, "Index", ctx, u)
	ret0, _ := ret[0].([]*group.Group)
	ret1, _ := ret[1].([]*group.Group)
	ret2, _ := ret[2].(error)
	return ret0, ret1, ret2
}

// Index indicates an expected call of Index
func (mr *MockGroupServiceMockRecorder) Index(ctx, u interface{}) *gomock.Call {
	mr.mock.ctrl.T.Helper()
	return mr.mock.ctrl.RecordCallWithMethodType(mr.mock, "Index", reflect.TypeOf((*MockGroupService)(nil).Index), ctx, u)
}

// Show mocks base method
func (m *MockGroupService) Show(ctx context.Context, groupID string) (*group.Group, error) {
	m.ctrl.T.Helper()
	ret := m.ctrl.Call(m, "Show", ctx, groupID)
	ret0, _ := ret[0].(*group.Group)
	ret1, _ := ret[1].(error)
	return ret0, ret1
}

// Show indicates an expected call of Show
func (mr *MockGroupServiceMockRecorder) Show(ctx, groupID interface{}) *gomock.Call {
	mr.mock.ctrl.T.Helper()
	return mr.mock.ctrl.RecordCallWithMethodType(mr.mock, "Show", reflect.TypeOf((*MockGroupService)(nil).Show), ctx, groupID)
}

// Create mocks base method
func (m *MockGroupService) Create(ctx context.Context, g *group.Group) (*group.Group, error) {
	m.ctrl.T.Helper()
	ret := m.ctrl.Call(m, "Create", ctx, g)
	ret0, _ := ret[0].(*group.Group)
	ret1, _ := ret[1].(error)
	return ret0, ret1
}

// Create indicates an expected call of Create
func (mr *MockGroupServiceMockRecorder) Create(ctx, g interface{}) *gomock.Call {
	mr.mock.ctrl.T.Helper()
	return mr.mock.ctrl.RecordCallWithMethodType(mr.mock, "Create", reflect.TypeOf((*MockGroupService)(nil).Create), ctx, g)
}

// Update mocks base method
func (m *MockGroupService) Update(ctx context.Context, g *group.Group, userIDs []string) (*group.Group, error) {
	m.ctrl.T.Helper()
	ret := m.ctrl.Call(m, "Update", ctx, g, userIDs)
	ret0, _ := ret[0].(*group.Group)
	ret1, _ := ret[1].(error)
	return ret0, ret1
}

// Update indicates an expected call of Update
func (mr *MockGroupServiceMockRecorder) Update(ctx, g, userIDs interface{}) *gomock.Call {
	mr.mock.ctrl.T.Helper()
	return mr.mock.ctrl.RecordCallWithMethodType(mr.mock, "Update", reflect.TypeOf((*MockGroupService)(nil).Update), ctx, g, userIDs)
}

// AddUsers mocks base method
func (m *MockGroupService) AddUsers(ctx context.Context, groupID string, userIDs []string) (*group.Group, error) {
	m.ctrl.T.Helper()
	ret := m.ctrl.Call(m, "AddUsers", ctx, groupID, userIDs)
	ret0, _ := ret[0].(*group.Group)
	ret1, _ := ret[1].(error)
	return ret0, ret1
}

// AddUsers indicates an expected call of AddUsers
func (mr *MockGroupServiceMockRecorder) AddUsers(ctx, groupID, userIDs interface{}) *gomock.Call {
	mr.mock.ctrl.T.Helper()
	return mr.mock.ctrl.RecordCallWithMethodType(mr.mock, "AddUsers", reflect.TypeOf((*MockGroupService)(nil).AddUsers), ctx, groupID, userIDs)
}

// RemoveUsers mocks base method
func (m *MockGroupService) RemoveUsers(ctx context.Context, groupID string, userIDs []string) (*group.Group, error) {
	m.ctrl.T.Helper()
	ret := m.ctrl.Call(m, "RemoveUsers", ctx, groupID, userIDs)
	ret0, _ := ret[0].(*group.Group)
	ret1, _ := ret[1].(error)
	return ret0, ret1
}

// RemoveUsers indicates an expected call of RemoveUsers
func (mr *MockGroupServiceMockRecorder) RemoveUsers(ctx, groupID, userIDs interface{}) *gomock.Call {
	mr.mock.ctrl.T.Helper()
	return mr.mock.ctrl.RecordCallWithMethodType(mr.mock, "RemoveUsers", reflect.TypeOf((*MockGroupService)(nil).RemoveUsers), ctx, groupID, userIDs)
}

// AddHiddenGroup mocks base method
func (m *MockGroupService) AddHiddenGroup(ctx context.Context, groupID string) ([]*group.Group, []*group.Group, error) {
	m.ctrl.T.Helper()
	ret := m.ctrl.Call(m, "AddHiddenGroup", ctx, groupID)
	ret0, _ := ret[0].([]*group.Group)
	ret1, _ := ret[1].([]*group.Group)
	ret2, _ := ret[2].(error)
	return ret0, ret1, ret2
}

// AddHiddenGroup indicates an expected call of AddHiddenGroup
func (mr *MockGroupServiceMockRecorder) AddHiddenGroup(ctx, groupID interface{}) *gomock.Call {
	mr.mock.ctrl.T.Helper()
	return mr.mock.ctrl.RecordCallWithMethodType(mr.mock, "AddHiddenGroup", reflect.TypeOf((*MockGroupService)(nil).AddHiddenGroup), ctx, groupID)
}

// RemoveHiddenGroup mocks base method
func (m *MockGroupService) RemoveHiddenGroup(ctx context.Context, groupID string) ([]*group.Group, []*group.Group, error) {
	m.ctrl.T.Helper()
	ret := m.ctrl.Call(m, "RemoveHiddenGroup", ctx, groupID)
	ret0, _ := ret[0].([]*group.Group)
	ret1, _ := ret[1].([]*group.Group)
	ret2, _ := ret[2].(error)
	return ret0, ret1, ret2
}

// RemoveHiddenGroup indicates an expected call of RemoveHiddenGroup
func (mr *MockGroupServiceMockRecorder) RemoveHiddenGroup(ctx, groupID interface{}) *gomock.Call {
	mr.mock.ctrl.T.Helper()
	return mr.mock.ctrl.RecordCallWithMethodType(mr.mock, "RemoveHiddenGroup", reflect.TypeOf((*MockGroupService)(nil).RemoveHiddenGroup), ctx, groupID)
}

// Destroy mocks base method
func (m *MockGroupService) Destroy(ctx context.Context, groupID string) error {
	m.ctrl.T.Helper()
	ret := m.ctrl.Call(m, "Destroy", ctx, groupID)
	ret0, _ := ret[0].(error)
	return ret0
}

// Destroy indicates an expected call of Destroy
func (mr *MockGroupServiceMockRecorder) Destroy(ctx, groupID interface{}) *gomock.Call {
	mr.mock.ctrl.T.Helper()
	return mr.mock.ctrl.RecordCallWithMethodType(mr.mock, "Destroy", reflect.TypeOf((*MockGroupService)(nil).Destroy), ctx, groupID)
}

// UploadThumbnail mocks base method
func (m *MockGroupService) UploadThumbnail(ctx context.Context, data []byte) (string, error) {
	m.ctrl.T.Helper()
	ret := m.ctrl.Call(m, "UploadThumbnail", ctx, data)
	ret0, _ := ret[0].(string)
	ret1, _ := ret[1].(error)
	return ret0, ret1
}

// UploadThumbnail indicates an expected call of UploadThumbnail
func (mr *MockGroupServiceMockRecorder) UploadThumbnail(ctx, data interface{}) *gomock.Call {
	mr.mock.ctrl.T.Helper()
	return mr.mock.ctrl.RecordCallWithMethodType(mr.mock, "UploadThumbnail", reflect.TypeOf((*MockGroupService)(nil).UploadThumbnail), ctx, data)
}

// ContainsUserID mocks base method
func (m *MockGroupService) ContainsUserID(ctx context.Context, g *group.Group, userID string) (bool, error) {
	m.ctrl.T.Helper()
	ret := m.ctrl.Call(m, "ContainsUserID", ctx, g, userID)
	ret0, _ := ret[0].(bool)
	ret1, _ := ret[1].(error)
	return ret0, ret1
}

// ContainsUserID indicates an expected call of ContainsUserID
func (mr *MockGroupServiceMockRecorder) ContainsUserID(ctx, g, userID interface{}) *gomock.Call {
	mr.mock.ctrl.T.Helper()
	return mr.mock.ctrl.RecordCallWithMethodType(mr.mock, "ContainsUserID", reflect.TypeOf((*MockGroupService)(nil).ContainsUserID), ctx, g, userID)
}
