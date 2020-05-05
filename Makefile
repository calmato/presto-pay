##################################################
# Container Commands - Run all containers
##################################################
.PHONY: setup install start

setup:
	cp $(PWD)/.env.temp $(PWD)/.env
	$(MAKE) build
	$(MAKE) install

build:
	docker-compose build

install:
	docker-compose run --rm user_api make setup

start:
	docker-compose up
