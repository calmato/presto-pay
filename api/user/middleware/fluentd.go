package middleware

import (
	"github.com/fluent/fluent-logger-golang/fluent"
	log "github.com/sirupsen/logrus"
)

// SendFluentd - Fluentdにログの転送
func SendFluentd(tag string, data interface{}) {
	fluentd, err := fluent.New(fluent.Config{})
	if err != nil {
		log.Error(err)
		return
	}
	fluentd.Close()

	if err = fluentd.Post(tag, data); err != nil {
		log.Error(err)
	}
}
