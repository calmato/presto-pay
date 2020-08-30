package messaging

import (
	"context"
	"fmt"

	firebase "firebase.google.com/go/v4"
	"firebase.google.com/go/v4/messaging"
)

// Messaging - Firestore Cloud Messaging (FCM)の構造体
type Messaging struct {
	Client *messaging.Client
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
	ctx context.Context, registrationTokens []string, data map[string]string,
) error {
	message := &messaging.MulticastMessage{
		Data:   data,
		Tokens: registrationTokens,
	}

	br, err := m.Client.SendMulticast(ctx, message)
	if err != nil {
		return err
	}

	fmt.Printf("FCM SendMuticast result is ...\nsuccess: %d\nfailure: %d\n", br.SuccessCount, br.FailureCount)
	return nil
}
