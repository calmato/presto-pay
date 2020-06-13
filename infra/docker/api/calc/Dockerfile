# ビルド用コンテナ
FROM golang:1.14.2-alpine as builder

ENV LANG C.UTF-8
ENV TZ Asia/Tokyo

WORKDIR /go/src/github.com/calmato/presto-pay/api/calc

RUN apk add --no-cache \
      git \
      make

ADD ./api/calc ./
RUN make build

# 実行用コンテナ
FROM alpine

RUN apk add --no-cache \
      ca-certificates

COPY --from=builder /go/src/github.com/calmato/presto-pay/api/calc/app ./app

EXPOSE 8080
CMD ["./app"]
