package user

import "context"

// UserUploader - UserUploaderインターフェース
type UserUploader interface {
	UploadThumbnail(ctx context.Context, data []byte) (string, error)
}
