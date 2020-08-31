package api

import (
	"context"

	fcm "github.com/calmato/presto-pay/api/calc/lib/firebase/message"
)

// NotificationClient - プッシュ通知用インターフェース
type NotificationClient interface {
	Send(ctx context.Context, deviceTokens []string, title string, body string)
}

// Client - プッシュ通知用の構造体
type Client struct {
	message *fcm.Message
}

// NewNotificationClient - NotificationClientの初期化
func NewNotificationClient(cm *fcm.Message) NotificationClient {
	return &Client{
		message: cm,
	}
}

func (c *client) Send(
	ctx context.Context, deviceTokens []string, title string, body string,
) error {
	message := &fcm.Data{
		Title: title,
		Body:  body,
	}

	if _, err := c.SendMulticast(ctx, deviceTokens, message); err != nil {
		return err
	}

	return nil
}
