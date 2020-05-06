#!/bin/sh

if [ ! -f /go/bin/mockgen ]; then
  echo "Not found mockgen. Please install this tool."
  echo "-> $ go get github.com/golang/mock/mockgen@latest"
  exit 1
fi

#############################
# Function
#############################
build_mock() {
  dir_name=$1
  target=$2

  paths=$(find internal/${dir_name} -name ${target} \
    | grep -vE 'test|base' \
    | awk -v var=${dir_name} '{ print substr($0, index($0, var)) }') \
    | sed -e "s/\/${target}$//"

  for path in ${paths}; do
    mkdir -p mocks/${path}
    mockgen -source internal/${path}/${target} -destination mock/${path}/${target}
  done
}

#############################
# Target
#############################
# --- Domain ---
build_mock 'domain' 'repository.go'
build_mock 'domain' 'service.go'
build_mock 'domain' 'uploader.go'
build_mock 'domain' 'validation.go'

# --- Application ---
build_mock 'application/validation' '*.go'
