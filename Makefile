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
.PHONY: swagger-start swagger-open swagger-edit

swagger-start:
	docker-compose -f docker-compose.swagger.yaml up

swagger-open:
	open http://localhost:8000

swagger-edit:
	open http://localhost:8001

##################################################
# Container Commands - Terraform
##################################################
.PHONY: terraform-stg-setup

terraform-stg-setup:
	docker-compose run --rm terraform make init ENV=stg

.PHONY: terraform-stg-lint
terraform-stg-lint:
	docker-compose run --rm terraform make fmt ENV=stg

.PHONY: terraform-stg-plan
terraform-stg-plan:
	docker-compose run --rm terraform make plan ENV=stg

.PHONY: terraform-stg-apply
terraform-stg-apply:
	docker-compose run --rm terraform make apply ENV=stg

.PHONY: terraform-stg-destroy
terraform-stg-destroy:
	docker-compose run --rm terraform make destroy ENV=stg
