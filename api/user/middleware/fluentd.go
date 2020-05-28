package middleware

import (
	"github.com/fluent/fluent-logger-golang/fluent"
	log "github.com/sirupsen/logrus"
)

// SendFluentd - Fluentdにログの転送
func SendFluentd(tag string, data interface{}) {
	config := fluent.Config{
		FluentHost: "fluentd",
		FluentPort: 24224,
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
