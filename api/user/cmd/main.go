package main

import (
	"net/http"

	"github.com/calmato/presto-pay/api/user/config"
	"github.com/calmato/presto-pay/api/user/registry"
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

	reg := registry.NewRegistry()

	// サーバ起動
	r := config.Router(reg)
	if err := http.ListenAndServe(":"+e.Port, r); err != nil {
		log.Panic(err)
	}
}
