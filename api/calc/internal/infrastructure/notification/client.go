package notification

import (
	"context"

	fcm "github.com/calmato/presto-pay/api/calc/lib/firebase/messaging"
)

// NotificationClient - プッシュ通知用インターフェース
type NotificationClient interface {
	Send(ctx context.Context, deviceTokens []string, title string, body string) error
}

// Client - プッシュ通知用の構造体
type Client struct {
	message *fcm.Messaging
}

// NewNotificationClient - NotificationClientの初期化
func NewNotificationClient(cm *fcm.Messaging) NotificationClient {
	return &Client{
		message: cm,
	}
}

// Send - プッシュ通知の送信
func (c *Client) Send(
	ctx context.Context, deviceTokens []string, title string, body string,
) error {
	message := &fcm.Data{
		Title: title,
		Body:  body,
	}

	if _, err := c.message.SendMulticast(ctx, deviceTokens, message); err != nil {
		return err
	}

	return nil
}
