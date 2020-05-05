GOLANGCI_VERSION := 1.26.0
REVEWDOG_VERSION := 0.9.17

BIN := $(abspath ./bin)
PACKAGES := $(shell go list ./... | grep -v mock)

.PHONY: setup run build fmt vet lint test review build-mocks

.PHONY: setup
setup:
	wget -O - -nv https://raw.githubusercontent.com/golangci/golangci-lint/master/install.sh | sh -s v${GOLANGCI_VERSION}
	wget -O - -q https://raw.githubusercontent.com/reviewdog/reviewdog/master/install.sh | sh -s v${REVEWDOG_VERSION}
	go get github.com/golang/mock/gomock
	go install github.com/golang/mock/mockgen

run:
	go run ./cmd/main.go

build:
	go build -o app ./cmd

fmt:
	! gofmt -d -s . | grep '^'

vet:
	go vet ./... $(PACKAGES)

lint:
	$(BIN)/golangci-lint run -c .golangci.yaml ./...

test:
	go test -v -cover ./internal/...

# REVIEWDOG_ARG
# * local -> -diff='git diff master'
# * pull request -> -reporter='github-pr-review'
.PHONY: review
review:
	$(BIN)/golangci-lint run -c .golangci.yaml --out-format line-number ./... \
	| $(BIN)/reviewdog -f=golangci-lint --name=golangci-lint ${REVIEWDOG_ARG}

.PHONY: build-mocks
build-mocks:
	$(BIN)/build-mocks.sh
