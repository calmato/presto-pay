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
	Base  string             `json:"base"`  // 為替レートのベース通貨日
	Date  string             `json:"date"`  // 為替レート評価
	Rates map[string]float64 `json:"rates"` // 為替レート
}

var (
	// Currencies - 為替レートの種類一覧
	Currencies = []string{
		"eur", // Euro
		"usd", // US dollar
		"jpy", // Japanese yen
		"bgn", // Bulgarian lev
		"czk", // Czech koruna
		"dkk", // Danish krone
		"gbp", // Pound sterling
		"huf", // Hungarian forint
		"pln", // Polish zloty
		"ron", // Romanian leu
		"sek", // Swedish krona
		"chf", // Swiss franc
		"isk", // Icelandic krona
		"nok", // Norwegian krone
		"hrk", // Croatian kuna
		"rub", // Russian rouble
		"try", // Turkish lira
		"aud", // Australian dollar
		"brl", // Brazilian real
		"cad", // Canadian dollar
		"cny", // Chinese yuan renmin
		"hkd", // Hong Kong dollar
		"idr", // Indonesian rupiah
		"ils", // Israeli shekel
		"inr", // Indian rupee
		"krw", // South Korean won
		"mxn", // Mexican peso
		"myr", // Malaysian ringgit
		"nzd", // New Zealand dollar
		"php", // Philippine peso
		"sgd", // Singapore dollar
		"thb", // Thai baht
		"zar", // South African rand
	}
)
