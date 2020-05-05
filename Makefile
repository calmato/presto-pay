##################################################
# Container Commands - All
##################################################
.PHONY: setup install start

setup:
	cp $(PWD)/.env.temp $(PWD)/.env
	docker-compose build
	$(MAKE) install

install:
	docker-compose run --rm user_api make setup

start:
	docker-compose up
