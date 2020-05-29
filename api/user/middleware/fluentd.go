package middleware

import (
	"github.com/fluent/fluent-logger-golang/fluent"
	log "github.com/sirupsen/logrus"
)

// SendFluentd - Fluentdにログの転送
func SendFluentd(tag string, data interface{}) {
	config := fluent.Config{
		FluentHost: "fluentd", // TODO: 環境変数へ
		FluentPort: 24224,     // TODO: 環境変数へ
	}

	fluentd, err := fluent.New(config)
	if err != nil {
		log.Error(err)
		return
	}
	fluentd.Close()

	if err = fluentd.Post(tag, data); err != nil {
		log.Error(err)
	}
}
