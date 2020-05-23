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
.PHONY: generate-parivate-key generate-deployment
generate-private-key:
	openssl req -x509 -nodes \
		-newkey rsa:2048 -days 365 \
		-keyout privkey.pem -out cert.pem \
		-subj "/CN=${EXTERNAL_IP}"

generate-deployment:
	cd ./${ENV}; sed 's/<PROJECT-ID>/${GCP_STORAGE_BUCKET_NAME}/' deployment-user-api.yaml.sample > deployment-user-api.yaml

#############################
# Update
#############################
.PHONY: update-deployment
update-deployment:
	kubectl patch deploy user-api -p "{\"spec\": {\"template\": {\"metadata\": {\"labels\": {\"date\": \"${CURRENT}\" }}}}}"