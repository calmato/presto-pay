##################################################
# 仮想クラウド・ネットワーク
##################################################
resource "oci_core_vcn" "this" {
  display_name   = var.vcn_name
  compartment_id = var.compartment_id

  cidr_block = var.vcn_cidr_block
  dns_label  = var.vcn_dns_label

  freeform_tags  = var.tags
}

##################################################
# 仮想クラウド・ネットワーク - サブネット
##################################################
resource "oci_core_subnet" "this" {
  for_each = local.subnets

  display_name   = each.value.name
  compartment_id = var.compartment_id
  vcn_id         = oci_core_vcn.this.id

  cidr_block                 = each.value.cidr_block
  availability_domain        = each.value.availability_domain
  prohibit_public_ip_on_vnic = each.value.prohibit_public_ip

  security_list_ids = concat(each.value.security_list_ids, [])

  freeform_tags  = var.tags
}

##################################################
# 仮想クラウド・ネットワーク - インターネット・ゲートウェイ
##################################################
resource "oci_core_internet_gateway" "this" {
  display_name   = var.igw_name
  compartment_id = var.compartment_id
  vcn_id         = oci_core_vcn.this.id

  enabled = true

  freeform_tags  = var.tags
}

##################################################
# 仮想クラウド・ネットワーク - ルートテーブル
##################################################
resource "oci_core_route_table" "public" {
  display_name   = var.public_route_table_name
  compartment_id = var.compartment_id
  vcn_id         = oci_core_vcn.this.id

  route_rules {
    description       = "Default Route"

    # 宛先
    description_type  = "CIDR_BLOCK"
    destination       = "0.0.0.0/0"

    # ネクストホップ
    network_entity_id = oci_core_internet_gateway.this.id
  }

  freeform_tags  = var.tags
}

resource "oci_core_route_table" "private" {
  display_name   = var.private_route_table_name
  compartment_id = var.compartment_id
  vcn_id         = oci_core_vcn.this.id

  freeform_tags  = var.tags
}

resource "oci_core_route_table_attachment" "this" {
  for_each = local.subnets

  subnet_id      = oci_core_subnet.this[each.key].id
  route_table_id = each.value.prohibit_public_ip ? oci_core_route_table.private.id : oci_core_route_table.public.id
}
