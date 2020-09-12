package main

import (
	"context"
	"net/http"

	"github.com/calmato/presto-pay/api/calc/config"
	"github.com/calmato/presto-pay/api/calc/internal/infrastructure/api"
	"github.com/calmato/presto-pay/api/calc/lib/firebase"
	"github.com/calmato/presto-pay/api/calc/lib/firebase/firestore"
	"github.com/calmato/presto-pay/api/calc/lib/firebase/messaging"
	"github.com/calmato/presto-pay/api/calc/lib/firebase/storage"
	"github.com/calmato/presto-pay/api/calc/middleware"
	"github.com/calmato/presto-pay/api/calc/registry"
	log "github.com/sirupsen/logrus"
	"google.golang.org/api/option"
)

func main() {
	ctx, cancel := context.WithCancel(context.Background())
	defer cancel()

	// ログ出力設定
	config.Logger()

	// 環境変数
	e, err := config.LoadEnvironment()
	if err != nil {
		log.Panic(err)
	}

	// ログ転送設定の初期化
	middleware.SetupFluent(e.FluentHost, e.FluentPort)

	// Firebaseの初期化
	opt := option.WithCredentialsFile(e.GoogleApplicationCredentials)

	fb, err := firebase.InitializeApp(ctx, nil, opt)
	if err != nil {
		panic(err)
	}

	// Firestore
	fs, err := firestore.NewClient(ctx, fb.App)
	if err != nil {
		panic(err)
	}
	defer fs.Close()

	// Cloud Storage
	cs, err := storage.NewClient(ctx, fb.App, e.GCPStorageBucketName)
	if err != nil {
		panic(err)
	}

	// Firebase Cloud Messaging
	fcm, err := messaging.NewClient(ctx, fb.App)
	if err != nil {
		panic(err)
	}

	// API Client
	ac := api.NewAPIClient(e.UserAPIURL)

	// メトリクス公開用サーバ起動
	mr := config.MetricsRouter()
	go func() {
		if err := http.ListenAndServe(":"+e.MetricsPort, mr); err != nil {
			log.Panic(err)
		}
	}()

	reg := registry.NewRegistry(fs, cs, fcm, ac)

	// サーバ起動
	r := config.Router(reg)
	if err := http.ListenAndServe(":"+e.Port, r); err != nil {
		log.Panic(err)
	}
}
