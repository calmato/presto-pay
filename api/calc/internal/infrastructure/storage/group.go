package storage

import (
	"context"

	"github.com/calmato/presto-pay/api/calc/internal/domain/group"
	gcs "github.com/calmato/presto-pay/api/calc/lib/firebase/storage"
)

type groupUploader struct {
	storage *gcs.Storage
}

// NewGroupUploader - GroupUploaderの生成
func NewGroupUploader(cs *gcs.Storage) group.GroupUploader {
	return &groupUploader{
		storage: cs,
	}
}

func (gu *groupUploader) UploadThumbnail(ctx context.Context, data []byte) (string, error) {
	thumbnailURL, err := gu.storage.Write(ctx, GroupThumbnailPath, data)
	if err != nil {
		return "", err
	}

	return thumbnailURL, nil
}
