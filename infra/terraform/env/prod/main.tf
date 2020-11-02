provider "oci" {
  tenancy_ocid     = var.tenancy_ocid
  region           = var.region

  user_ocid        = var.user_ocid
  fingerprint      = var.fingerprint
  private_key_path = var.private_key_path
}

locals {
  tags = {
    service = "presto-pay"
    env     = "prod"
  }
}

module "network" {
  source = "./../../modules/oci/network"

  compartment_id = var.compartment_ocid
  tags           = local.tags

  ##################################################
  # 仮想クラウド・ネットワーク
  ##################################################
  vcn_name       = "presto-pay-vcn"
  vcn_cidr_block = "192.168.120.0/24"
  vcn_dns_label  = ""

  ##################################################
  # 仮想クラウド・ネットワーク - サブネット
  ##################################################
  subnets = [
    {
      name                = "presto-pay-sub-lb"
      cidr_block          = "192.168.120.0/27"
      availability_domain = ""
      prohibit_public_ip  = false
    },
    {
      name                = "presto-pay-sub-pub"
      cidr_block          = "192.168.120.32/27"
      availability_domain = ""
      prohibit_public_ip  = false
    },
    {
      name                = "presto-pay-sub-pri"
      cidr_block          = "192.168.120.64/27"
      availability_domain = ""
      prohibit_public_ip  = true
    },
  ]

  ##################################################
  # 仮想クラウド・ネットワーク - インターネット・ゲートウェイ
  ##################################################
  igw_name = "presto-pay-igw"

  ##################################################
  # 仮想クラウド・ネットワーク - ルートテーブル
  ##################################################
  public_route_table_name  = "presto-pay-rt-pub"
  private_route_table_name = "presto-pay-rt-pri"

  ##################################################
  # 仮想クラウド・ネットワーク - セキュリティ・グループ
  ##################################################
  network_security_groups = [
    {
      name = "presto-pay-nsg-lb"
      rules = [
        {
          direction   = "INGRESS"
          stateless   = false
          description = "Allow SSH"
          protocol    = "6" # TCP
          source      = "0.0.0.0/0"
          destination = "192.168.120.0/27"
          port_from   = 10022
          port_to     = 10022
        },
        {
          direction   = "INGRESS"
          stateless   = false
          description = "Allow SSH"
          protocol    = "6" # TCP
          source      = "0.0.0.0/0"
          destination = "192.168.120.0/27"
          port_from   = 20022
          port_to     = 20022
        },
        {
          direction   = "INGRESS"
          stateless   = false
          description = "Allow HTTP"
          protocol    = "6" # TCP
          source      = "0.0.0.0/0"
          destination = "192.168.120.0/27"
          port_from   = 80
          port_to     = 80
        },
        {
          direction   = "INGRESS"
          stateless   = false
          description = "Allow HTTPS"
          protocol    = "6" # TCP
          source      = "0.0.0.0/0"
          destination = "192.168.120.0/27"
          port_from   = 443
          port_to     = 443
        },
        {
          direction   = "EGRESS"
          stateless   = false
          description = "Allow All"
          protocol    = "all"
          source      = "192.168.120.0/27"
          destination = "0.0.0.0/0"
          port_from   = "all"
          port_to     = "all"
        },
      ]
    },
    {
      name = "presto-pay-nsg-api"
      rules = [
        {
          direction   = "INGRESS"
          stateless   = false
          description = "Allow SSH"
          protocol    = "6" # TCP
          source      = "192.168.120.0/27"
          destination = "192.168.120.0/27"
          port_from   = 22
          port_to     = 22
        },
        {
          direction   = "INGRESS"
          stateless   = false
          description = "Allow HTTP"
          protocol    = "6" # TCP
          source      = "0.0.0.0/0"
          destination = "192.168.120.0/27"
          port_from   = 80
          port_to     = 80
        },
        {
          direction   = "INGRESS"
          stateless   = false
          description = "Allow HTTP"
          protocol    = "6" # TCP
          source      = "0.0.0.0/0"
          destination = "192.168.120.0/27"
          port_from   = 8000
          port_to     = 8000
        },
        {
          direction   = "INGRESS"
          stateless   = false
          description = "Allow HTTP"
          protocol    = "6" # TCP
          source      = "0.0.0.0/0"
          destination = "192.168.120.0/27"
          port_from   = 8080
          port_to     = 8080
        },
        {
          direction   = "EGRESS"
          stateless   = false
          description = "Allow All"
          protocol    = "all"
          source      = "192.168.120.0/27"
          destination = "0.0.0.0/0"
          port_from   = "all"
          port_to     = "all"
        },
      ]
    },
  ]
}
