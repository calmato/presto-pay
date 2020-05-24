# インフラストラクチャ - Terraform

[root](./../../../README.md) 
/ [13_infrastructure](./../README.md) 
/ [32_terraform](./README.md)

## コード作成

* env配下にディレクトリ作成
  > $ make create ENV=[環境名 e.g.)prod]

* ディレクトリの移動
  > $ cd ./env/[環境名]

* 構成ファイルの編集
  * 以下ファイルの `xxxxxx` と記載の箇所を編集
    * backend.tf
    * terraform.tfvars
    * main.tf
