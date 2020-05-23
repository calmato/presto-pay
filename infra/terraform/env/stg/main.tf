provider "google" {
  project = var.project_id
  region  = "asia-northeast1"
}

module "this" {
  source = "./../../modules"

  location = "asia-northeast1-a"

  #################################################
  # GKE Cluster
  #################################################
  gke_cluster_name        = "presto-pay-cluster"
  gke_cluster_description = "presto-pay application cluster for staging"

  gke_cluster_min_master_version = "1.14.10-gke.17"

  #################################################
  # GKE Node
  #################################################
  gke_node_configs = [
    {
      name         = "presto-pay-node"
      count        = 1
      preemptible  = false
      machine_type = "f1-micro"
      disk_type    = "pd-standard"
      disk_size_gb = 10
    },
    {
      name         = "presto-pay-spot-node"
      count        = 2
      preemptible  = true
      machine_type = "g1-small"
      disk_type    = "pd-standard"
      disk_size_gb = 20
    },
  ]

  #################################################
  # GCE Global Address
  #################################################
  create_global_address = true

  global_address_name = "presto-pay-ip-address"
}
