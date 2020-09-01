package messaging

import (
	"context"

	firebase "firebase.google.com/go/v4"
	"firebase.google.com/go/v4/messaging"
	log "github.com/sirupsen/logrus"
)

// Messaging - Firestore Cloud Messaging (FCM)の構造体
type Messaging struct {
	Client *messaging.Client
}

type Data struct {
	Title string
	Body  string
}

// NewClient - FCMに接続
func NewClient(ctx context.Context, app *firebase.App) (*Messaging, error) {
	client, err := app.Messaging(ctx)
	if err != nil {
		return nil, err
	}

	return &Messaging{client}, nil
}

// SendMulticast - 複数端末へのプッシュ通知
func (m *Messaging) SendMulticast(
	ctx context.Context, registrationTokens []string, d *Data,
) (*messaging.BatchResponse, error) {
	data := map[string]string{
		"title": d.Title,
		"body":  d.Body,
	}

	notification := &messaging.Notification{
		Title: d.Title,
		Body:  d.Body,
	}

	message := &messaging.MulticastMessage{
		Data:         data,
		Notification: notification,
		Tokens:       registrationTokens,
	}

	_, err := m.Client.SendMulticast(ctx, message)

	// TODO: remove
	fields := log.Fields{
		"message": message,
	}
	log.WithFields(fields).Debug("test")

	return nil, err
}
