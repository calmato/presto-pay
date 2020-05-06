package user

import "context"

type UserUploader interface {
	UploadThumbnail(ctx context.Context, data []byte) (string, error)
}
