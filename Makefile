.PHONY: ca certs \
		clean


.DEFAULT_GOAL := help

PKI_DIR="./pki/"

help:
	@echo "Usage:"
	@echo " make ca      				# Create CA key/certificate."
	@echo " make certs   				# Create server key/certificate."
	@echo " make clean   		    # Clean"
	@echo " make help    				# Help message"

ca:
	@sh makeca pki/CA_etcd etcd-ca
	@sudo ln -sf ${PWD}/${PKI_DIR}/CA_etcd /etc/pki/CA_etcd
	@echo ""
	@echo "========="
	@echo "Succ    "
	@echo "========="
	@echo "Last to complete it:" 
	@echo "  - Append ./conf/openssl.conf to /etc/pki/tls/openssl.conf"
	@echo "  - Configure /etc/pki/tls/openss.conf, set [policy_match] countryName,stateOrProvinceName,organizationName,organizationalUnitName to \"optional\"."

certs:
	@sh makecert CA_etcd ${PKI_DIR}/certs/ etcd
	@sh makecert CA_etcd ${PKI_DIR}/certs/ etcd-client
	@sh makecert CA_etcd ${PKI_DIR}/certs/ etcd-peer


clean:
	@rm -rfv pki

