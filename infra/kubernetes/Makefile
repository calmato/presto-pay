CURRENT := $(shell date "+%Y%m%d-%H%M%S")

#############################
# Common
#############################
.PHONY: get-external-ip

get-external-ip:
	kubectl get service envoy -o jsonpath='{.status.loadBalancer.ingress[0].ip}'

#############################
# Setup
#############################
.PHONY: generate-parivate-key generate-manifest

generate-private-key:
	openssl req -x509 -nodes \
		-newkey rsa:2048 -days 365 \
		-keyout privkey.pem -out cert.pem \
		-subj "/CN=${EXTERNAL_IP}"

generate-manifest:
	cd ./${ENV}; sed 's/<PROJECT-ID>/${GCP_STORAGE_BUCKET_NAME}/' default/deployment-user-api.yaml.temp > default/deployment-user-api.yaml
	cd ./${ENV}; sed 's/<PROJECT-ID>/${GCP_STORAGE_BUCKET_NAME}/' default/deployment-calc-api.yaml.temp > default/deployment-calc-api.yaml
	cd ./${ENV}; sed 's/<PROJECT-ID>/${GCP_STORAGE_BUCKET_NAME}/' default/cronjob-batch.yaml.temp > default/cronjob-batch.yaml

#############################
# Update
#############################
.PHONY: update-deployment update-deployment-all

update-deployment:
	kubectl patch deploy ${APP_NAME} -p "{\"spec\": {\"template\": {\"metadata\": {\"labels\": {\"date\": \"${CURRENT}\" }}}}}"

update-cronjob:
	kubectl patch deploy ${APP_NAME} -p "{\"spec\": {\"template\": {\"metadata\": {\"labels\": {\"date\": \"${CURRENT}\" }}}}}"

update-manifest-all:
	$(MAKE) update-deployment APP_NAME=user-api
	$(MAKE) update-deployment APP_NAME=calc-api
	$(MAKE) update-cronjob APP_NAME=batch
