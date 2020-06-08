module github.com/calmato/presto-pay/api/calc

go 1.14

require (
	cloud.google.com/go/firestore v1.2.0
	cloud.google.com/go/storage v1.6.0
	firebase.google.com/go v3.12.1+incompatible
	github.com/bmizerany/assert v0.0.0-20160611221934-b7ed37b82869 // indirect
	github.com/calmato/gran/api/user v0.0.0-20200511134047-18998a6ce8a1 // indirect
	github.com/calmato/presto-pay/api/user v0.0.0-20200607104734-4491a3f72edc
	github.com/fluent/fluent-logger-golang v1.5.0
	github.com/gin-gonic/gin v1.6.3
	github.com/go-playground/validator/v10 v10.2.0
	github.com/google/uuid v1.1.1
	github.com/kelseyhightower/envconfig v1.4.0
	github.com/prometheus/client_golang v1.6.0
	github.com/rs/cors v1.7.0
	github.com/sirupsen/logrus v1.6.0
	golang.org/x/xerrors v0.0.0-20191204190536-9bdfabe68543
	google.golang.org/api v0.23.0
)
