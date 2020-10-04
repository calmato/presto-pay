package redis

import (
	"context"
	"fmt"
	"strings"

	redis "github.com/go-redis/redis/v8"
	log "github.com/sirupsen/logrus"
)

// Client - Redisの構造体
type Client struct {
	DB *redis.Client
}

// NewClient - Redis Clientの初期化
func NewClient(ctx context.Context, host string, port string, db int) (*Client, error) {
	addr := strings.Join([]string{host, ":", port}, "")

	rdb := redis.NewClient(&redis.Options{
		Addr: addr,
		DB:   db,
	})

	pong, err := rdb.Ping(ctx).Result()

	fields := log.Fields{
		"result": fmt.Sprintf("Connection result to Redis: %v\n", pong),
	}
	log.WithFields(fields).Debug("redis")

	if err != nil {
		return nil, err
	}

	return &Client{rdb}, nil
}

// Set - Redisへ保存
func (client *Client) Set(ctx context.Context, key string, value interface{}, expiration int) error {
	return client.DB.Set(ctx, key, value, 0).Err()
}

// Get - Redisから値を取得
func (client *Client) Get(ctx context.Context, key string) (string, error) {
	return client.DB.Get(ctx, key).Result()
}
