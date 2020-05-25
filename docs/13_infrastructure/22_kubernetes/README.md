# インフラストラクチャ - Kubernetes

[root](./../../../README.md) 
/ [13_infrastructure](./../README.md) 
/ [22_kubernetes](./README.md)

## GKEでkubectlが使えるとこまでの手順

* Google Cloud SDKのインストール
  * Mac OSの場合
    > https://cloud.google.com/sdk/docs/quickstart-macos?hl=ja
  * Ubuntuの場合
    > https://cloud.google.com/sdk/docs/quickstart-debian-ubuntu?hl=ja
  * CentOSの場合
    > https://cloud.google.com/sdk/docs/quickstart-redhat-centos?hl=ja

* kubectlのインストール
  * Mac OSの場合
    > https://kubernetes.io/ja/docs/tasks/tools/install-kubectl/#install-kubectl-on-macos
  * Ubuntu/CentOSの場合
    > https://kubernetes.io/ja/docs/tasks/tools/install-kubectl/#install-kubectl-on-linux

* GKEクラスタの証明書を取得
  * GCPのコンソールを開く
    > https://console.cloud.google.com/home/dashboard
  * `ナビゲーションメニュー` > `コンピューティング` > `Kubernetes Engine` > `クラスタ`
  * 該当クラスタの `接続` ボタンをクリック
  * 表示されるコマンドをターミナルへコピペし実行

* GKEクラスタの情報を取得できることを確認
  > $ kubectl get all -o wide

* e.g.) User APIのログを取得
  > $ kubectl logs deployment/user-api | grep -v health

---
* 環境構築 (log確認用)
- google-cloud-sdkをいれる  
> ./install.sh  
yを選択  
> gcloud init  
2を選択（1~2)  
4を選択　(1~5)  
y  
34を選択 (1~50)  
> gcloud container clusters get-credentials presto-pay-cluster --zone asia-northeast1-a --project presto-pay-dev  
> kubectl get all -o wide  
> kubectl logs deployment/user-api | grep -v health  
これで確認できるようになる

## 参考

* [Google Cloud SDKのインストール](https://cloud.google.com/sdk/install?hl=ja)
* [kubectlのインストール](https://kubernetes.io/ja/docs/tasks/tools/install-kubectl/)
