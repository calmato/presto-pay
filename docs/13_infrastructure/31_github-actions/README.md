# インフラストラクチャ - Github Actions

[root](./../../../README.md) 
/ [13_infrastructure](./../README.md) 
/ [31_github-actions](./README.md)

---

## GCPへのデプロイ

* GCPでの初期設定 
  1. Container RegistryのAPIを有効化
  2. Github Actions用のサービスアカウントを作成
    * ロールは、 `ストレージ管理者` を選択
  3. サービスアカウント作成じに取得した認証ファイルをbase64エンコード
    > $ cat [認証ファイル名].json | base64

* Githubでの初期設定
  1. Settings > Secrets
  2. base64エンコードした認証ファイル情報を登録
