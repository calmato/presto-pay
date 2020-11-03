# SSL証明書の作成

## OCI CLIをインストール

* 以下コマンドを実行し、OCI CLIをインストール
  > $ bash -c "$(curl -L https://raw.githubusercontent.com/oracle/oci-cli/master/scripts/install/install.sh)"

* OCIの初期設定を実施
  > $ oci setup config

## Let's EncryptでSSL証明書を作成

* 必要となるパッケージのインストール
  > $ yum install -y \  
  >     epel-release \  
  >     jq

* certbotのインストール
  > $ yum install -y \  
  >     certbot \  
  >     python-certbot-apache

* SSL証明書の取得
  > $ sudo certbot certonly \  
  >     --manual \  
  >     --domain [ドメイン名] \  
  >     --email [email] \  
  >     --agree-tos \  
  >     --manual-public-ip-logging-ok \  
  >     --preferred-challenges dns

## SSL証明書をアップロード

* LBの情報を取得
  > $ lb_ocid=$(oci lb load-balancer list --compartment-id [compartment-id] | jq -r .data[0].id)

* SSL証明書をアップロード
  > $ oci lb certificate create \  
  >     --certificate-name lets_encrypt \  
  >     --load-balancer-id $lb_ocid \  
  >     --private-key-file /etc/letsencrypt/live/[domain]/privkey.pem \  
  >     --public-certificate-file /etc/letsencrypt/live/[domain]/cert.pem
