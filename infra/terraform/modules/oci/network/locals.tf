locals {
  subnets = {
    for s in var.subnets : s.name => {
      name                = s.name
      cidr_block          = s.cidr_block
      availability_domain = s.availability_domain
      prohibit_public_ip  = s.prohibit_public_ip
      security_list_ids   = []
    }
  }

  network_security_groups = {
    for nsg in var.network_security_groups : nsg.name => {
      name = nsg.name
    }
  }

  rules = flatten([
    for nsg in var.network_security_groups : [
      for r in nsg.rules : {
        name                      = "${nsg.name}-${r.direction}-${r.protocol}-${r.port_from}-${r.port_to}"
        network_security_group_id = oci_core_network_security_group.this[nsg.name].id
        description               = upper(r.description)
        direction                 = upper(r.direction)
        stateless                 = r.stateless
        source_type               = "CIDR_BLOCK"
        source                    = r.source
        destination_type          = "CIDR_BLOCK"
        destination               = r.destination
        protocol                  = r.protocol
        icmp_type                 = "all"
        icmp_code                 = "all"
        port_from                 = r.port_from
        port_to                   = r.port_to
      }
    ]
  ])

  network_security_group_rules = {
    for r in local.rules : r.name => r
  }
}
