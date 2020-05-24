#################################################
# Common
#################################################
variable "project_id" {
  default = ""
}

variable "location" {
  default = "asia-northeast1-a"
}

#################################################
# GKE Cluster
#################################################
variable "gke_cluster_name" {
  description = "GKE クラスタ名"
  default     = ""
}

variable "gke_cluster_description" {
  description = "GKE クラスタ説明"
  default     = ""
}

variable "gke_cluster_min_master_version" {
  description = "GKE クラスタ最低バージョン"
  default     = "1.14.10-gke.36"
}

variable "gke_cluster_ipv4_cidr" {
  description = "GKE PodのCIDR"
  default     = null
}

#################################################
# GKE Node
#################################################
variable "gke_node_name_prefix" {
  description = "GKE ノード名の接頭辞"
  default     = ""
}

variable "gke_node_count" {
  description = "GKE ノード数"
  type        = number
  default     = 1
}

variable "gke_node_machine_type" {
  description = "GKE ノードマシンタイプ"
  default     = "f1-micro"
}
