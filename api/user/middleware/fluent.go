package middleware

import (
	"github.com/fluent/fluent-logger-golang/fluent"
	log "github.com/sirupsen/logrus"
)

var (
	fluentHost = ""
	fluentPort = 0
)

// SetupFluent - ログ転送先の設定
func SetupFluent(host string, port int) {
	fluentHost = host
	fluentPort = port
}

// SendFluent - Fluentへログを転送
func SendFluent(tag string, data interface{}) {
	config := fluent.Config{
		FluentHost: fluentHost,
		FluentPort: fluentPort,
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
