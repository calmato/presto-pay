# バックエンド - 設計

[root](./../../../README.md) 
/ [12_backend](./../README.md) 
/ [01_design](./README.md)

# 各種ドキュメント

## 各種正規表現

* Email
  * Regex: ```^(?:(?:(?:(?:[a-zA-Z]|\\d|[!#\\$%&'\\*\\+\\-\\/=\\?\\^_`{\\|}~]|[\\x{00A0}-\\x{D7FF}\\x{F900}-\\x{FDCF}\\x{FDF0}-\\x{FFEF}])+(?:\\.([a-zA-Z]|\\d|[!#\\$%&'\\*\\+\\-\\/=\\?\\^_`{\\|}~]|[\\x{00A0}-\\x{D7FF}\\x{F900}-\\x{FDCF}\\x{FDF0}-\\x{FFEF}])+)*)|(?:(?:\\x22)(?:(?:(?:(?:\\x20|\\x09)*(?:\\x0d\\x0a))?(?:\\x20|\\x09)+)?(?:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x7f]|\\x21|[\\x23-\\x5b]|[\\x5d-\\x7e]|[\\x{00A0}-\\x{D7FF}\\x{F900}-\\x{FDCF}\\x{FDF0}-\\x{FFEF}])|(?:(?:[\\x01-\\x09\\x0b\\x0c\\x0d-\\x7f]|[\\x{00A0}-\\x{D7FF}\\x{F900}-\\x{FDCF}\\x{FDF0}-\\x{FFEF}]))))*(?:(?:(?:\\x20|\\x09)*(?:\\x0d\\x0a))?(\\x20|\\x09)+)?(?:\\x22))))@(?:(?:(?:[a-zA-Z]|\\d|[\\x{00A0}-\\x{D7FF}\\x{F900}-\\x{FDCF}\\x{FDF0}-\\x{FFEF}])|(?:(?:[a-zA-Z]|\\d|[\\x{00A0}-\\x{D7FF}\\x{F900}-\\x{FDCF}\\x{FDF0}-\\x{FFEF}])(?:[a-zA-Z]|\\d|-|\\.|~|[\\x{00A0}-\\x{D7FF}\\x{F900}-\\x{FDCF}\\x{FDF0}-\\x{FFEF}])*(?:[a-zA-Z]|\\d|[\\x{00A0}-\\x{D7FF}\\x{F900}-\\x{FDCF}\\x{FDF0}-\\x{FFEF}])))\\.)+(?:(?:[a-zA-Z]|[\\x{00A0}-\\x{D7FF}\\x{F900}-\\x{FDCF}\\x{FDF0}-\\x{FFEF}])|(?:(?:[a-zA-Z]|[\\x{00A0}-\\x{D7FF}\\x{F900}-\\x{FDCF}\\x{FDF0}-\\x{FFEF}])(?:[a-zA-Z]|\\d|-|\\.|~|[\\x{00A0}-\\x{D7FF}\\x{F900}-\\x{FDCF}\\x{FDF0}-\\x{FFEF}])*(?:[a-zA-Z]|[\\x{00A0}-\\x{D7FF}\\x{F900}-\\x{FDCF}\\x{FDF0}-\\x{FFEF}])))\\.?$```

* Password
  * Regex: `^[a-zA-Z0-9_!@#$_%^&*.?()-=+]*$`

* Currency
  * Regex: `eur|usd|jpy|bgn|czk|dkk|gbp|huf|pln|ron|sek|chf|isk|nok|hrk|rub|try|aud|brl|cad|cny|hkd|idr|ils|inr|krw|mxn|myr|nzd|php|sgd|thb|zar`

* Datetime
  * Format: `2006-01-02 15:04:05`

## リクエスト値のバリデーション

### User

|        Field         |      Japanese      |                          Validation                          |
| :------------------- | :----------------- | :----------------------------------------------------------- |
| UserID               | ユーザーUID        | ・Required                                                   |
| Name                 | 表示名             | ・Required<br/>・Length: n <= 32                             |
| Username             | ニックネーム       | ・Required<br/>・Length: n <= 32                             |
| Email                | メールアドレス     | ・Required<br/>・Length: n <= 256<br/>・Format: Email        |
| Thumbnail            | サムネイル         | ・Format: base64                                             |
| Password             | パスワード         | ・Required<br/>・Length: 6 <= n <= 32<br/>・Format: Password |
| PasswordConfirmation | パスワード(確認用) | ・Required<br>・Equal: Password                              |
| InstanceID           | 端末ID             | ・Required                                                   |

### Group

|   Field   |    Japanese    |               Validation               |
| :-------- | :------------- | :------------------------------------- |
| Name      | グループ名     | ・Required<br/>・Length: n <= 64       |
| Thumbnail | サムネイル     | ・Format: base64                       |
| UserIDs   | ユーザーID一覧 | ・ Length(Array): n <= 64<br/>・Unique |

### Payment

|     Field      |     Japanese     |                          Validation                          |
| :------------- | :--------------- | :----------------------------------------------------------- |
| ID             | 支払いUID        | ・Required                                                   |
| Name           | 支払い名         | ・Required<br/>・Length: n <= 64                             |
| Currency       | 通貨             | ・Required<br/>・Format: Currency                            |
| Comment        | 説明             | ・Length: n <= 256                                           |
| PositivePayers | 支払い済ユーザー | ・Length(Array): 0 <= n <= 64                                |
| NegativePayers | 未支払いユーザー | ・Length(Array): 0 <= n <= 64                                |
| Tags           | タグ一覧         | ・Length(Array): 0 <= n <= 32<br/>・Length(Element): n <= 32 |
| Images         | イメージ一覧     | ・Length(Array): 0 <= n <= 32<br/>・Format: base64             |
| PaidAt         | 支払日時         | ・Required<br/>・Format: Datetime                            |

#### Payment - Payers

| Field  |     Japanese     |        Validation        |
| :----- | :--------------- | :----------------------- |
| ID     | ユーザーUID      | ・Required               |
| Amount | 支払額           | ・Range: 0 < n <= 999999 |
| IsPaid | 支払い完了フラグ |                          |
