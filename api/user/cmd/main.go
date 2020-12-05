package main

import (
	"context"
	"net/http"

	"github.com/calmato/presto-pay/api/user/config"
	"github.com/calmato/presto-pay/api/user/lib/firebase"
	"github.com/calmato/presto-pay/api/user/lib/firebase/authentication"
	"github.com/calmato/presto-pay/api/user/lib/firebase/firestore"
	"github.com/calmato/presto-pay/api/user/lib/firebase/storage"
	"github.com/calmato/presto-pay/api/user/middleware"
	"github.com/calmato/presto-pay/api/user/registry"
	log "github.com/sirupsen/logrus"
	"golang.org/x/oauth2/google"
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
	// opt := option.WithCredentialsFile(e.GoogleApplicationCredentials)
	credentials, err := google.CredentialsFromJSON(ctx, []byte(e.GCPServiceKeyJSON))
	if err != nil {
		panic(err)
	}

	opt := option.WithCredentials(credentials)

	fb, err := firebase.InitializeApp(ctx, nil, opt)
	if err != nil {
		panic(err)
	}

	// Firebase Authentication
	fa, err := authentication.NewClient(ctx, fb.App)
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

	// メトリクス公開用サーバ起動
	mr := config.MetricsRouter()
	go func() {
		if err := http.ListenAndServe(":"+e.MetricsPort, mr); err != nil {
			log.Panic(err)
		}
	}()

	reg := registry.NewRegistry(fa, fs, cs)

	// サーバ起動
	r := config.Router(reg)
	if err := http.ListenAndServe(":"+e.Port, r); err != nil {
		log.Panic(err)
	}
}
