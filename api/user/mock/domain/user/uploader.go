// Code generated by MockGen. DO NOT EDIT.
// Source: internal/domain/user/uploader.go

// Package mock_user is a generated GoMock package.
package mock_user

import (
	context "context"
	gomock "github.com/golang/mock/gomock"
	reflect "reflect"
)

// MockUserUploader is a mock of UserUploader interface
type MockUserUploader struct {
	ctrl     *gomock.Controller
	recorder *MockUserUploaderMockRecorder
}

// MockUserUploaderMockRecorder is the mock recorder for MockUserUploader
type MockUserUploaderMockRecorder struct {
	mock *MockUserUploader
}

// NewMockUserUploader creates a new mock instance
func NewMockUserUploader(ctrl *gomock.Controller) *MockUserUploader {
	mock := &MockUserUploader{ctrl: ctrl}
	mock.recorder = &MockUserUploaderMockRecorder{mock}
	return mock
}

// EXPECT returns an object that allows the caller to indicate expected use
func (m *MockUserUploader) EXPECT() *MockUserUploaderMockRecorder {
	return m.recorder
}

// UploadThumbnail mocks base method
func (m *MockUserUploader) UploadThumbnail(ctx context.Context, data []byte) (string, error) {
	m.ctrl.T.Helper()
	ret := m.ctrl.Call(m, "UploadThumbnail", ctx, data)
	ret0, _ := ret[0].(string)
	ret1, _ := ret[1].(error)
	return ret0, ret1
}

// UploadThumbnail indicates an expected call of UploadThumbnail
func (mr *MockUserUploaderMockRecorder) UploadThumbnail(ctx, data interface{}) *gomock.Call {
	mr.mock.ctrl.T.Helper()
	return mr.mock.ctrl.RecordCallWithMethodType(mr.mock, "UploadThumbnail", reflect.TypeOf((*MockUserUploader)(nil).UploadThumbnail), ctx, data)
}
