package config

import (
	"github.com/kelseyhightower/envconfig"
	"golang.org/x/xerrors"
)

// Environment - システム内で利用する環境変数の構造体
type Environment struct {
	Port string `envconfig:"PORT" default:"8080"`
}

// LoadEnvironment - 環境変数の取得
func LoadEnvironment() (Environment, error) {
	env := Environment{}
	if err := envconfig.Process("", &env); err != nil {
		return env, xerrors.Errorf("Failed to LoadEnvironment: %w", err)
	}

	return env, nil
}
