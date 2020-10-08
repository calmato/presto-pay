package main

import (
	"context"
	"encoding/json"
	"fmt"
	"io/ioutil"
	"log"
	"net/http"
	"reflect"
	"strings"

	"github.com/go-redis/redis/v8"
	"github.com/kelseyhightower/envconfig"
)

// Response - APIからのレスポンス用構造体
type Response struct {
	Rates *Rates `json:"rates"` // 為替レート
	Base  string `json:"base"`  // 為替レートのベース通貨
	Date  string `json:"date"`  // 為替レート評価日
}

// Rates - 為替レート用構造体
type Rates struct {
	EUR float64 `json:"eur"` // Euro
	USD float64 `json:"usd"` // US dollar
	JPY float64 `json:"jpy"` // Japanese yen
	BGN float64 `json:"bgn"` // Bulgarian lev
	CZK float64 `json:"czk"` // Czech koruna
	DKK float64 `json:"dkk"` // Danish krone
	GBP float64 `json:"gbp"` // Pound sterling
	HUF float64 `json:"huf"` // Hungarian forint
	PLN float64 `json:"pln"` // Polish zloty
	RON float64 `json:"ron"` // Romanian leu
	SEK float64 `json:"sek"` // Swedish krona
	CHF float64 `json:"chf"` // Swiss franc
	ISK float64 `json:"isk"` // Icelandic krona
	NOK float64 `json:"nok"` // Norwegian krone
	HRK float64 `json:"hrk"` // Croatian kuna
	RUB float64 `json:"rub"` // Russian rouble
	TRY float64 `json:"try"` // Turkish lira
	AUD float64 `json:"aud"` // Australian dollar
	BRL float64 `json:"brl"` // Brazilian real
	CAD float64 `json:"cad"` // Canadian dollar
	CNY float64 `json:"cny"` // Chinese yuan renminbi
	HKD float64 `json:"hkd"` // Hong Kong dollar
	IDR float64 `json:"idr"` // Indonesian rupiah
	ILS float64 `json:"ils"` // Israeli shekel
	INR float64 `json:"inr"` // Indian rupee
	KRW float64 `json:"krw"` // South Korean won
	MXN float64 `json:"mxn"` // Mexican peso
	MYR float64 `json:"myr"` // Malaysian ringgit
	NZD float64 `json:"nzd"` // New Zealand dollar
	PHP float64 `json:"php"` // Philippine peso
	SGD float64 `json:"sgd"` // Singapore dollar
	THB float64 `json:"thb"` // Thai baht
	ZAR float64 `json:"zar"` // South African rand
}

// Environment - システム内で利用する環境変数用の構造体
type Environment struct {
	RedisHost string `envconfig:"REDIS_HOST" default:"redis"`
	RedisPort string `envconfig:"REDIS_PORT" default:"6379"`
	RedisDB   int    `envconfig:"REDIS_DB" default:"0"`
}

const (
	// 為替レート取得先APIのURL
	exchangeRatesAPIURL = "https://api.exchangeratesapi.io/latest?base=JPY"
)

var ctx = context.Background()

func main() {
	// 環境変数の取得
	env, err := loadEnvironment()
	if err != nil {
		panic(err)
	}

	// Redisクライアントの生成
	rdb, err := newRedisClient(env)
	if err != nil {
		panic(err)
	}

	// APIへのリクエスト処理
	res, err := getResponse()
	if err != nil {
		panic(err)
	}

	// Redisへ値を保存
	if err = setRedis(rdb, "date", res.Date, 0); err != nil {
		panic(err)
	}

	if err = setRedis(rdb, "base", res.Base, 0); err != nil {
		panic(err)
	}

	// TODO: 関数化
	rv := reflect.ValueOf(res.Rates).Elem()
	rt := rv.Type()

	for i := 0; i < rt.NumField(); i++ {
		k := rt.Field(i).Tag.Get("json")
		v := rv.Field(i).Interface().(float64) // TODO: reflectから型取得できるように

		log.Printf("key: %v, value: %v", k, v)

		if err = setRedis(rdb, k, v, 0); err != nil {
			panic(err)
		}
	}
}

/*
 * ###########################
 *  Environment
 * ###########################
 */
func loadEnvironment() (*Environment, error) {
	env := &Environment{}
	if err := envconfig.Process("", env); err != nil {
		return nil, err
	}

	return env, nil
}

/*
 * ###########################
 *  HTTP Client
 * ###########################
 */
func getResponse() (*Response, error) {
	resp, err := http.Get(exchangeRatesAPIURL)
	if err != nil {
		return nil, err
	}
	defer resp.Body.Close()

	body, err := ioutil.ReadAll(resp.Body)
	if err != nil {
		return nil, err
	}

	res := &Response{}
	if err = json.Unmarshal(body, res); err != nil {
		return nil, err
	}

	return res, nil
}

/*
 * ###########################
 *  Redis
 * ###########################
 */
func newRedisClient(env *Environment) (*redis.Client, error) {
	addr := strings.Join([]string{env.RedisHost, ":", env.RedisPort}, "")

	rdb := redis.NewClient(&redis.Options{
		Addr: addr,
		DB:   env.RedisDB,
	})

	pong, err := rdb.Ping(ctx).Result()

	fmt.Printf("Connection result to Redis: %v\n", pong)
	if err != nil {
		return nil, err
	}

	return rdb, nil
}

func setRedis(rdb *redis.Client, key string, value interface{}, expiration int) error {
	return rdb.Set(ctx, key, value, 0).Err()
}
