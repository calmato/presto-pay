# Presto Pay

割り勘アプリ

![User API(Golang) Build and Test](https://github.com/calmato/presto-pay/workflows/User%20API(Golang)%20Build%20and%20Test/badge.svg)
![User API(Golang) Reviewdog](https://github.com/calmato/presto-pay/workflows/User%20API(Golang)%20Reviewdog/badge.svg)  
![Terraform(stg) Test](https://github.com/calmato/presto-pay/workflows/Terraform(stg)%20Test/badge.svg)  

![User API(Golang) Deploy to Staging](https://github.com/calmato/presto-pay/workflows/User%20API(Golang)%20Deploy%20to%20Staging/badge.svg)

## 各種設定

<details open>
<summary>依存関係</summary>

* API
  * Golang: 1.14.2
* Envoy
  * 1.14.1
* Swagger
  * Swagger Editor: 3.8.1
  * Swagger UI: 3.25.1
* Terraform
  * Terraform: 0.12.25
</details>

<details open>
<summary>環境構築</summary>

### リポジトリのダウンロード

> $ git clone https://github.com/calmato/presto-pay.git

> $ cd ./presto-pay

### コンテナの初期設定

* コンテナの作成

> $ make setup

* .envファイルの編集
  * Firebaseの操作権限があるサービスアカウントの秘密鍵を `secretディレクトリ` にコピペ
    * -> `secretsディレクトリ` にコピペしたファイル名に合わせて `GOOGLE_APPLICATION_CREDENTIALS`, `TERRAFORM_CREDENTIALS` を編集

```.env
GOOGLE_APPLICATION_CREDENTIALS=/secrets/xxxxxx-firebase-adminsdk-xxxxxx.json
TERRAFORM_CREDENTIALS=/secrets/xxxxxx-terraform-xxxxxx.json
GCP_STORAGE_BUCKET_NAME=xxxxxx.appspot.com
```

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
| make swagger-open | * API仕様書を見る                                                                        |
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
