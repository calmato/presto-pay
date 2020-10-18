provider "oci" {
  tenancy_ocid     = var.tenancy_ocid
  region           = var.region

  user_ocid        = var.user_ocid
  fingerprint      = var.fingerprint
  private_key_path = var.private_key_path
}

locals {
  tags = {
    service = "presto-pay-dev"
    env     = "dev"
  }
}

module "network" {
  source = "./../../modules/oci/network"

  compartment_id = var.compartment_ocid
  tags           = local.tags

  ##################################################
  # 仮想クラウド・ネットワーク
  ##################################################
  vcn_name       = "presto-pay-dev-vcn"
  vcn_cidr_block = "192.168.100.0/24"
  vcn_dns_label  = ""

  ##################################################
  # 仮想クラウド・ネットワーク - サブネット
  ##################################################
  subnets = [
    {
      name                = "presto-pay-dev-sub-pub"
      cidr_block          = "192.168.100.0/27"
      availability_domain = ""
      prohibit_public_ip  = false
    },
    {
      name                = "presto-pay-dev-sub-pri"
      cidr_block          = "192.168.100.32/27"
      availability_domain = ""
      prohibit_public_ip  = true
    },
  ]

  ##################################################
  # 仮想クラウド・ネットワーク - インターネット・ゲートウェイ
  ##################################################
  igw_name = "presto-pay-dev-igw"

  ##################################################
  # 仮想クラウド・ネットワーク - ルートテーブル
  ##################################################
  public_route_table_name  = "presto-pay-dev-rt-pub"
  private_route_table_name = "presto-pay-dev-rt-pri"

  ##################################################
  # 仮想クラウド・ネットワーク - セキュリティ・グループ
  ##################################################
  network_security_groups = [
    {
      name = "presto-pay-dev-nsg-lb"
      rules = [
        {
          direction   = "INGRESS"
          stateless   = false
          description = "Allow SSH"
          protocol    = "6" # TCP
          source      = "0.0.0.0/0"
          destination = "192.168.100.0/27"
          port_from   = 22
          port_to     = 22
        },
        {
          direction   = "INGRESS"
          stateless   = false
          description = "Allow HTTP"
          protocol    = "6" # TCP
          source      = "0.0.0.0/0"
          destination = "192.168.100.0/27"
          port_from   = 80
          port_to     = 80
        },
        {
          direction   = "INGRESS"
          stateless   = false
          description = "Allow HTTPS"
          protocol    = "6" # TCP
          source      = "0.0.0.0/0"
          destination = "192.168.100.0/27"
          port_from   = 443
          port_to     = 443
        },
        {
          direction   = "EGRESS"
          stateless   = false
          description = "Allow All"
          protocol    = "all"
          source      = "192.168.100.0/27"
          destination = "0.0.0.0/0"
          port_from   = "all"
          port_to     = "all"
        },
      ]
    },
    {
      name = "presto-pay-dev-nsg-api"
      rules = [
        {
          direction   = "INGRESS"
          stateless   = false
          description = "Allow SSH"
          protocol    = "6" # TCP
          source      = "0.0.0.0/0"
          destination = "192.168.100.0/27"
          port_from   = 22
          port_to     = 22
        },
        {
          direction   = "INGRESS"
          stateless   = false
          description = "Allow HTTP"
          protocol    = "6" # TCP
          source      = "0.0.0.0/0"
          destination = "192.168.100.0/27"
          port_from   = 80
          port_to     = 80
        },
        {
          direction   = "EGRESS"
          stateless   = false
          description = "Allow All"
          protocol    = "all"
          source      = "192.168.100.0/27"
          destination = "0.0.0.0/0"
          port_from   = "all"
          port_to     = "all"
        },
      ]
    },
  ]
}
