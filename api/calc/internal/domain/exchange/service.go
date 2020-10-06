package exchange

import "context"

// ExchangeService - ExchangeServiceインターフェース
type ExchangeService interface {
	Index(ctx context.Context) (*ExchangeRates, error)
	Show(ctx context.Context, baseCurrency string, currency string) (*Exchange, error)
}
