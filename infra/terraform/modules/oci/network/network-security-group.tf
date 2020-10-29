##################################################
# ネットワーク・セキュリティ・グループ
##################################################
resource "oci_core_network_security_group" "this" {
  for_each = local.network_security_groups

  display_name   = each.value.name
  compartment_id = var.compartment_id
  vcn_id         = oci_core_vcn.this.id

  freeform_tags  = var.tags
}

##################################################
# ネットワーク・セキュリティ・グループ - ルール
##################################################
resource "oci_core_network_security_group_security_rule" "this" {
  for_each = local.network_security_group_rules

  network_security_group_id = each.value.network_security_group_id

  description = each.value.description

  direction = each.value.direction
  stateless = each.value.stateless

  source_type = each.value.source_type
  source      = each.value.source

  destination_type = each.value.destination_type
  destination      = each.value.destination

  protocol = each.value.protocol

  # --- ICMP options ---
  dynamic "icmp_options" {
    for_each = each.value.protocol == "1" ? [1] : []

    content {
      type = each.value.icmp_type
      code = each.value.icmp_code
    }
  }

  # --- UDP options ---
  dynamic "udp_options" {
    for_each = each.value.protocol == "17" ? [1] : []

    content {
      dynamic "source_port_range" {
        for_each = each.value.direction == "EGRESS" ? [1] : []

        content {
          max = each.value.port_from
          min = each.value.port_to
        }
      }

      dynamic "destination_port_range" {
        for_each = each.value.direction == "INGRESS" ? [1] : []

        content {
          max = each.value.port_from
          min = each.value.port_to
        }
      }
    }
  }

  # --- TCP options ---
  dynamic "tcp_options" {
    for_each = each.value.protocol == "6" ? [1] : []

    content {
      dynamic "source_port_range" {
        for_each = each.value.direction == "EGRESS" ? [1] : []

        content {
          max = each.value.port_from
          min = each.value.port_to
        }
      }

      dynamic "destination_port_range" {
        for_each = each.value.direction == "INGRESS" ? [1] : []

        content {
          max = each.value.port_from
          min = each.value.port_to
        }
      }
    }
  }
}
