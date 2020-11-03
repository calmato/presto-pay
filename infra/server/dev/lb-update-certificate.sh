#!bin/bash

##############################
# Arguments
##############################
tenancy_ocid=""
domain_name=""

cert_prefix="lets_encrypt"
private_key_path="/etc/letsencrypt/live/$domain_name/privkey.pem"
public_key_path="/etc/letsencrypt/live/$domain_name/cert.pem"

##############################
# Main
##############################
# SSL証明書名の重複が不可なため、証明書名末尾に日付を付与
cert_suffix=$(date "+%Y%m%d")
cert_name="$cert_prefix-$cert_suffix"

# OCI CLIより、LBのOCIDを取得
lb_ocid=$(oci lb load-balancer list --compartment-id $tenancy_ocid | jq -r .data[0].id)

# OCI CLIを利用して、LB用のSSL証明書を更新
oci lb certificate create \
  --certificate-name $cert_name \
  --load-balancer-id $lb_ocid \
  --private-key-file $private_key_path \
  --public-certificate-file $public_key_path
