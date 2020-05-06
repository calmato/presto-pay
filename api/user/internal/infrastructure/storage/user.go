package storage

import (
	"context"

	gcs "github.com/calmato/presto-pay/api/user/lib/firebase/storage"
)

type userUploader struct {
	storage *gcs.Storage
}

// NewUserUploader - UserUploaderの生成
func NewUserUploader(cs *gcs.Storage) user.UserUploader {
	return &userUploader{
		storage: cs,
	}
}

func (uu *userUploader) UploadThumbnail(ctx context.Context, data []byte) (string, error) {
	thumbnailURL, err := uu.storage.Write(ctx, UserThumbnailPath, data)
	if err != nil {
		return "", err
	}

	return thumbnailURL, nil
}
