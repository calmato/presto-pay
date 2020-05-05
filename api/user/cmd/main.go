package main

import (
	"net/http"

	"github.com/calmato/presto-pay/api/user/config"
	log "github.com/sirupsen/logrus"
)

func main() {
	// ログ出力設定
	config.Logger()

	// 環境変数
	e, err := config.LoadEnvironment()
	if err != nil {
		log.Panic(err)
	}

	// サーバ起動
	r := config.Router()
	if err := http.ListenAndServe(":"+e.Port, r); err != nil {
		log.Panic(err)
	}
}
