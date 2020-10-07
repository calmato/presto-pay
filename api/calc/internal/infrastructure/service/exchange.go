package service

import (
	"context"

	"github.com/calmato/presto-pay/api/calc/internal/domain"
	"github.com/calmato/presto-pay/api/calc/internal/domain/exchange"
)

type exchangeService struct {
	exchangeRepository exchange.ExchangeRepository
}

// NewExchangeService - ExchangeServiceの生成
func NewExchangeService(er exchange.ExchangeRepository) exchange.ExchangeService {
	return &exchangeService{
		exchangeRepository: er,
	}
}

func (es *exchangeService) Index(ctx context.Context) (*exchange.ExchangeRates, error) {
	ers, err := es.exchangeRepository.Index(ctx)
	if err != nil {
		return nil, domain.ErrorInDatastore.New(err)
	}

	return ers, nil
}

func (es *exchangeService) Show(
	ctx context.Context, baseCurrency string, currency string,
) (*exchange.Exchange, error) {
	baseRate, date, err := es.exchangeRepository.Show(ctx, baseCurrency)
	if err != nil {
		return nil, domain.ErrorInDatastore.New(err)
	}

	rate, _, err := es.exchangeRepository.Show(ctx, currency)
	if err != nil {
		return nil, domain.ErrorInDatastore.New(err)
	}

	e := &exchange.Exchange{
		Date:         date,
		BaseCurrency: baseCurrency,
		BaseRate:     baseRate,
		Currency:     currency,
		Rate:         rate,
	}

	return e, nil
}
