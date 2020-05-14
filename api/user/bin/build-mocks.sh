#!/bin/sh

if [ ! -f /go/bin/mockgen ]; then
  echo "Not found mockgen. Please install this tool."
  echo "-> $ go get github.com/golang/mock/mockgen@latest"
  exit 1
fi

#############################
# Variables
#############################
ignore_lists='test|base|domain/validation.go'

#############################
# Function
#############################
build_mock() {
  target=$1

  mockgen -source internal/${target} -destination mock/${target}
}

domain() {
  dir_name='domain'
  file_name=$1

  paths=$(find internal/${dir_name} -name ${file_name} \
    | grep -vE ${ignore_lists} \
    | awk -v var=${dir_name} '{ print substr($0, index($0, var)) }' \
    | sed -e "s/\/${file_name}$//")

  for path in ${paths}; do
    mkdir -p mock/${path}
    build_mock "${path}/${file_name}"
  done
}

application() {
  dir_name="application/$1"
  mkdir -p mock/${dir_name}

  paths=$(find internal/${dir_name} -name '*.go' \
    | grep -vE ${ignore_lists} \
    | awk -v var=${dir_name} '{ print substr($0, index($0, var)) }')

  for path in ${paths}; do
    build_mock ${path}
  done
}

#############################
# Target
#############################
# --- Domain ---
domain 'repository.go'
domain 'service.go'
domain 'uploader.go'
domain 'validation.go'

# --- Application ---
application 'validation'
