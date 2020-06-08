package group

import "context"

// GroupUploader - GroupUploaderインターフェース
type GroupUploader interface {
	UploadThumbnail(tx context.Context, data []byte) (string, error)
}
