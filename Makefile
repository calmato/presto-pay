##################################################
# Container Commands - Run all containers
##################################################
.PHONY: setup build install start log

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

stop:
	docker-compose stop

logs:
	docker-compose logs

##################################################
# Container Commands - Swagger
##################################################
.PHONY: swagger-open swagger-edit

swagger-open:
	open http://localhost:8000

swagger-edit:
	open http://localhost:8001
