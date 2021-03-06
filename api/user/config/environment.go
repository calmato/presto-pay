package config

import (
	"github.com/kelseyhightower/envconfig"
	"golang.org/x/xerrors"
)

// Environment - システム内で利用する環境変数の構造体
type Environment struct {
	Port                 string `envconfig:"PORT" default:"8080"`
	MetricsPort          string `envconfig:"METRICS_PORT" default:"8081"`
	FluentHost           string `envconfig:"FLUENT_HOST" default:"fluent"`
	FluentPort           int    `envconfig:"FLUENT_PORT" default:"24224"`
	GCPStorageBucketName string `envconfig:"GCP_STORAGE_BUCKET_NAME" required:"true"`
	GCPServiceKeyJSON    string `envconfig:"GCP_SERVICE_KEY_JSON" required:"true"`
	// GoogleApplicationCredentials string `envconfig:"GOOGLE_APPLICATION_CREDENTIALS" required:"true"`
}

// LoadEnvironment - 環境変数の取得
func LoadEnvironment() (Environment, error) {
	env := Environment{}
	if err := envconfig.Process("", &env); err != nil {
		return env, xerrors.Errorf("Failed to LoadEnvironment: %w", err)
	}

	return env, nil
}
