package exchange

// Exchange - Exchangeエンティティ
type Exchange struct {
	Date         string  `json:"date"`      // 為替レート評価日
	BaseCurrency string  `json:"base"`      // ベース通貨
	BaseRate     float64 `json:"baseRates"` // ベース通貨の為替レート
	Currency     string  `json:"currency"`  // 通貨
	Rate         float64 `json:"rates"`     // 為替レート
}

// ExchangeRates - 為替レートエンティティ
type ExchangeRates struct {
	Base  string `json:"base"`  // 為替レートのベース通貨日
	Date  string `json:"date"`  // 為替レート評価
	Rates *Rates `json:"rates"` // 為替レート
}

// Rates - 為替レート一覧エンティティ
type Rates struct {
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
