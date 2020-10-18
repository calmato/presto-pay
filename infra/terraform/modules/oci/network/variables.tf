##################################################
# 共通
##################################################
variable "compartment_id" {}
variable "tags" {}

##################################################
# 仮想クラウド・ネットワーク
##################################################
variable "vcn_name" {
  description = "仮想ネットワーク名"
  default     = ""
}

variable "vcn_cidr_block" {
  description = "CIDRブロック"
  default     = ""
}

variable "vcn_dns_label" {
  description = "DNSラベル (英字と数字のみを含めて、英字で始める必要があります。最大15文字)"
  default     = ""
}

##################################################
# 仮想クラウド・ネットワーク - サブネット
##################################################
variable "subnets" {
  description = "サブネット一覧"
  type = list(object({
    name                = string # サブネット名
    cidr_block          = string # CIDRブロック
    availability_domain = string # "": リージョナル, "xlwA:AP-TOKYO-AD-1": 可用性ドメイン固有
    prohibit_public_ip  = bool   # true: プライベートサブネット, false: パブリックサブネット
  }))
  default = []
}

##################################################
# 仮想クラウド・ネットワーク - インターネット・ゲートウェイ
##################################################
variable "igw_name" {
  description = "インターネットゲートウェイ名"
  default     = ""
}

##################################################
# 仮想クラウド・ネットワーク - ルートテーブル
##################################################
variable "public_route_table_name" {
  description = "ルートテーブル名(パブリックサブネット用)"
  default     = ""
}

variable "private_route_table_name" {
  description = "ルートテーブル名(プライベートサブネット用)"
  default     = ""
}

##################################################
# ネットワーク・セキュリティ・グループ
##################################################
# プロトコル番号一覧
# - ICMP ("1"), TCP ("6"), UDP ("17"), and ICMPv6 ("58")
# - http://www.iana.org/assignments/protocol-numbers/protocol-numbers.xhtml
variable "network_security_groups" {
  description = "ネットワークセキュリティグループ一覧"
  type = list(object({
    name  = string # NSG名
    rules = list(object({
      direction   = string # 方向 (INGRESS/EGRESS)
      stateless   = bool   # ステートレス設定
      protocol    = string # プロトコル番号 or all
      source      = string # ソース
      destination = string # 宛先
      port_from   = string # ポート範囲 (開始位置)
      port_to     = string # ポート範囲 (終了位置)
      description = string # NSGルール説明
    })) # NSGルール一覧
  }))
  default = []
}
