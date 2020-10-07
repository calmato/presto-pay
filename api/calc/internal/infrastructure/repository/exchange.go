package repository

import (
	"context"
	"strconv"
	"strings"

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

func (er *exchangeRepository) Index(ctx context.Context) (*exchange.ExchangeRates, error) {
	rs := map[string]float64{}

	// 為替レート評価日の取得
	date, err := er.redis.Get(ctx, "date")
	if err != nil {
		return nil, err
	}

	// ベースとなる為替レートの取得
	base, err := er.redis.Get(ctx, "base")
	if err != nil {
		return nil, err
	}

	// 為替レート一覧の取得
	for _, c := range exchange.Currencies {
		rateStr, err := er.redis.Get(ctx, c)
		if err != nil {
			continue
		}

		rate, err := strconv.ParseFloat(rateStr, 64)
		if err != nil {
			continue
		}

		rs[c] = rate
	}

	ers := &exchange.ExchangeRates{
		Date:  date,
		Base:  strings.ToLower(base),
		Rates: rs,
	}

	return ers, nil
}

func (er *exchangeRepository) Show(ctx context.Context, currency string) (float64, string, error) {
	// 為替レート評価日の取得
	date, err := er.redis.Get(ctx, "date")
	if err != nil {
		return 0, "", err
	}

	// 為替レートの取得
	rateStr, err := er.redis.Get(ctx, currency)
	if err != nil {
		return 0, "", err
	}

	rate, err := strconv.ParseFloat(rateStr, 64)
	if err != nil {
		return 0, "", err
	}

	return rate, date, nil
}
