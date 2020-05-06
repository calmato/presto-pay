package main

import (
	"context"
	"net/http"

	"github.com/calmato/presto-pay/api/user/config"
	"github.com/calmato/presto-pay/api/user/lib/firebase"
	"github.com/calmato/presto-pay/api/user/lib/firebase/authentication"
	"github.com/calmato/presto-pay/api/user/lib/firebase/firestore"
	"github.com/calmato/presto-pay/api/user/registry"
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

	// Firebaseの初期化
	opt := option.WithCredentialsFile(e.GoogleApplicationCredentials)

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

	reg := registry.NewRegistry(fa, fs)

	// サーバ起動
	r := config.Router(reg)
	if err := http.ListenAndServe(":"+e.Port, r); err != nil {
		log.Panic(err)
	}
}