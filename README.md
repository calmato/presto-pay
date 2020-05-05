# Presto Pay

割り勘アプリ

## 各種設定

<details open>
<summary>依存関係</summary>

* WIP
</details>

<details open>
<summary>環境構築</summary>

### リポジトリのダウンロード

> $ git clone https://github.com/calmato/presto-pay.git

> $ cd ./presto-pay

### コンテナの初期設定

* コンテナの作成

> $ make setup

* [WIP] .envファイルの編集

### コンテナの起動

> $ make start

</details>

## その他

<details>
<summary>コマンド一覧</summary>

|     コマンド      |                                                                                        |
| :---------------- | :------------------------------------------------------------------------------------- |
| make setup        | * 初回のみ実行                                                                         |
| make install      | * コンテナ内にライブラリをインストール<br>* ライブラリを更新する際はこのコマンドを使用 |
| make start        | * コンテナの起動                                                                       |
| make stop         | * コンテナの停止                                                                       |
| make logs         | * コンテナのログを取得                                                                 |
| make swagger-open | API仕様書を見る                                                                        |
</details>

<details>
<summary>各種ドキュメント</summary>

* [01_specification](./docs/01_specification/README.md)
* [11_frontend](./docs/11_frontend/README.md)
  * [01_native](./docs/11_frontend/01_native/README.md)
    * [01_design](./docs/11_frontend/01_native/01_design/README.md)
    * [11_ios](./docs/11_frontend/01_native/11_ios/README.md)
    * [12_android](./docs/11_frontend/01_native/12_android/README.md)
  * [02_web](./docs/11_frontend/02_web/README.md)
    * [01_design](./docs/11_frontend/02_web/01_design/README.md)
* [12_backend](./docs/12_backend/README.md)
  * [01_design](./docs/12_backend/01_design/README.md)
  * [11_user](./docs/12_backend/11_user/README.md)
  * [12_calc](./docs/12_backend/12_calc/README.md)
  * [13_payment](./docs/12_backend/13_payment/README.md)
  * [21_swagger](./docs/12_backend/21_swagger/README.md)
* [13_infrastructure](./docs/13_infrastructure/README.md)
  * [01_design](./docs/13_infrastructure/01_design/README.md)
  * [11_gcp](./docs/13_infrastructure/11_gcp/README.md)
  * [12_firebase](./docs/13_infrastructure/12_firebase/README.md)
  * [21_docker](./docs/13_infrastructure/21_docker/README.md)
  * [22_kubernetes](./docs/13_infrastructure/22_kubernetes/README.md)
  * [31_github-actions](./docs/13_infrastructure/31_github-actions/README.md)
  * [32_terraform](./docs/13_infrastructure/32_terraform/README.md)
</details>
