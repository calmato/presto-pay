package service

import (
	"context"
	"strings"

	"github.com/calmato/presto-pay/api/calc/internal/domain"
	"github.com/calmato/presto-pay/api/calc/internal/domain/exchange"
	"github.com/calmato/presto-pay/api/calc/internal/infrastructure/api"
)

type exchangeService struct {
	apiClient api.APIClient
}

// NewExchangeService - ExchangeServiceの生成
func NewExchangeService(ac api.APIClient) exchange.ExchangeService {
	return &exchangeService{
		apiClient: ac,
	}
}

func (es *exchangeService) Index(ctx context.Context) (*exchange.ExchangeRates, error) {
	ers, err := es.apiClient.ShowExchangeRates(ctx)
	if err != nil {
		return nil, domain.ErrorInDatastore.New(err)
	}

	return ers, nil
}

func (es *exchangeService) Show(
	ctx context.Context, baseCurrency string, currency string,
) (*exchange.Exchange, error) {
	ers, err := es.apiClient.ShowExchangeRates(ctx)
	if err != nil {
		return nil, domain.ErrorInDatastore.New(err)
	}

	bcu := strings.ToUpper(baseCurrency)
	cu := strings.ToUpper(currency)

	e := &exchange.Exchange{
		Date:         ers.Date,
		BaseCurrency: bcu,
		BaseRate:     ers.Rates[bcu],
		Currency:     cu,
		Rate:         ers.Rates[cu],
	}

	return e, nil
}
