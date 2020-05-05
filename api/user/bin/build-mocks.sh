#!/bin/sh

if [ ! -f /go/bin/mockgen ]; then
  echo "Not found mockgen. Please install this tool."
  echo "-> $ go get github.com/golang/mock/mockgen@latest"
  exit 1
fi

# --- Main ---
main() {
  dir_name=$1
  mkdir -p mock/${dir_name}

  paths=$(find internal/$1 -name *.go \
    | grep -vE 'test|base' \
    | awk -v var=${dir_name} '{ print substr($0, index($0, var)) }')

  for path in ${paths}; do
    mockgen -source internal/${path} -destination mock/${path}
  done
}

# --- Domain ---
main 'domain/repository'
main 'domain/service'
main 'domain/validation'
main 'domain/uploader'

# --- Application ---
main 'application/validation'
