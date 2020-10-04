package repository

import (
	"context"
	"strconv"

	"github.com/calmato/presto-pay/api/calc/internal/domain/exchange"
	"github.com/calmato/presto-pay/api/calc/lib/redis"
)

type exchangeRepository struct {
	redis *redis.Client
}

// NewExchangeRepository - ExchangeRepositoryの生成
func NewExchangeRepository(r *redis.Client) exchange.ExchangeRepository {
	// Redisにしたたのは勉強したかったから, そのうちFirestoreに変える
	return &exchangeRepository{
		redis: r,
	}
}

// TODO: 後で実装
func (er *exchangeRepository) Index(ctx context.Context) (*exchange.ExchangeRates, error) {
	return nil, nil
}

func (er *exchangeRepository) Show(ctx context.Context, currency string) (float64, string, error) {
	// 為替レート評価日の取得
	date, err := er.redis.Get(ctx, "date")
	if err != nil {
		return 0, "N/A", err
	}

	// 為替レートの取得
	rateStr, err := er.redis.Get(ctx, "date")
	if err != nil {
		return 0, "N/A", err
	}

	rate, err := strconv.ParseFloat(rateStr, 64)
	if err != nil {
		return 0, "N/A", err
	}

	return rate, date, nil
}
