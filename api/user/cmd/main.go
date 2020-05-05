package main

import (
	"net/http"

	"github.com/calmato/presto-pay/api/user/config"
)

func main() {
	// サーバ起動
	r := config.Router()
	if err := http.ListenAndServe(":8080", r); err != nil {
		panic(err)
	}
}
