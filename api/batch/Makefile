.PHONY: run

PACKAGES := $(shell go list ./...)

run:
	go run ./main.go

build:
	go build -o app .

fmt:
	! gofmt -d -s . | grep '^'

vet:
	go vet ./... $(PACKAGES)
