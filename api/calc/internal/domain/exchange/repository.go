package exchange

import "context"

// ExchangeRepository - ExchangeRepositoryインターフェース
type ExchangeRepository interface {
	Index(ctx context.Context) (*ExchangeRates, error)
	Show(ctx context.Context, currency string) (float64, string, error)
}
