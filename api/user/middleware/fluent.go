package middleware

import (
	"github.com/fluent/fluent-logger-golang/fluent"
	log "github.com/sirupsen/logrus"
)

// SendFluent - Fluent Bitへログを転送
func SendFluent(tag string, data interface{}) {
	config := fluent.Config{
		FluentHost: "fluent", // TODO: Edit
		FluentPort: 24224,    // TODO: Edit
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
