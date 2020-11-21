#!bin/bash

##############################
# Arguments
##############################
container_name="presto_pay_batch"

##############################
# Main
##############################
# 対象のコンテナID一覧を取得
container_ids=$(docker ps -aq -f "name=$container_name")

# 対象のコンテナの停止
for i in $container_ids; do
  docker stop $i
  echo "stop container: $i"
done
