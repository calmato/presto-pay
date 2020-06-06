# インフラストラクチャ - Kubernetes

[root](./../../../README.md) 
/ [13_infrastructure](./../README.md) 
/ [22_kubernetes](./README.md) 
/ [grafana](./grafana.md)

## 検証環境のGrafanaの起動方法

* kubectlを用いてポート転送
  > $ kubectl port-forward svc/grafana  -n monitoring 3000:3000
* Grafanaをブラウザで開く
  > $ open http://localhost:3000

## Grafanaの設定

* Configuration > Data Sources > Add data sources
  * Prometheusの設定
    * HTTP URL: `http://prometheus:9090`
  * Likiの設定
    * HTTP URL: `http://loki:3100`

* Create > Import
  * Import via grafana.com: `10000`

## 各種モニタリング

* Kubernetesクラスタのモニタリング
  * Dashboards > Manage > `Cluster Monitoring for Kubernetes`

* ロギング
  * Explore > Loki

