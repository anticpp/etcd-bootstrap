#!/bin/bash


## Configure DISCOVERY address
DISCOVERY_HOST="192.168.56.10"
DISCOVERY_CLIENT_PORT=3379
DISCOVERY_PEER_PORT=3380

## 
DISCOVERY_ADVERTISE_CLIENT_URLs="http://${DISCOVERY_HOST}:${DISCOVERY_CLIENT_PORT}"
DISCOVERY_LISTEN_CLIENT_URLs="http://${DISCOVERY_HOST}:${DISCOVERY_CLIENT_PORT},http://127.0.0.1:${DISCOVERY_CLIENT_PORT}"
DISCOVERY_ADVERTISE_PEER_URLs="http://127.0.0.1:${DISCOVERY_PEER_PORT}"
DISCOVERY_LISTEN_PEER_URLs="http://127.0.0.1:${DISCOVERY_PEER_PORT}"

##
ETCD_BIN=/usr/local/bin/etcd/etcd

## Functions
function etcd() {
	[ $# -ne 3 ] && echo "Usage: $0 <name> <host> <uuid>" && return
    name=$1
    host=$2
    uuid=$3
    echo "Bootstraping ${name}/${host} with TOKEN $uuid ..."

    ETCD_ENABLE_V2=true ${ETCD_BIN} --log-level=debug --data-dir=data.etcd --name ${name} \
        --advertise-client-urls "https://${host}:2379" \
        --listen-client-urls "https://${host}:2379,https://127.0.0.1:2379" \
        --initial-advertise-peer-urls "https://${host}:2380" \
        --listen-peer-urls "https://${host}:2380" \
        --client-cert-auth \
        --trusted-ca-file=./pki/CA_etcd/cacert.pem \
        --cert-file=./pki/certs/etcd.pem \
        --key-file=./pki/certs/etcd-key.pem \
        --peer-client-cert-auth \
        --peer-trusted-ca-file=./pki/CA_etcd/cacert.pem \
        --peer-cert-file=./pki/certs/etcd-peer.pem \
        --peer-key-file=./pki/certs/etcd-peer-key.pem \
        --discovery http://${DISCOVERY_HOST}:${DISCOVERY_CLIENT_PORT}/v2/keys/_etcd/registry/${uuid}
}

function discovery() {
    echo "Start discovery service..."
    ETCD_ENABLE_V2=true ${ETCD_BIN} --log-level=debug --data-dir=data.disc.etcd --name discovery0 \
        --advertise-client-urls "${DISCOVERY_ADVERTISE_CLIENT_URLs}" \
        --listen-client-urls "${DISCOVERY_LISTEN_CLIENT_URLs}" \
        --initial-advertise-peer-urls "${DISCOVERY_ADVERTISE_PEER_URLs}" \
        --listen-peer-urls "${DISCOVERY_LISTEN_PEER_URLs}" \
        --initial-cluster-state "new" \
        --initial-cluster "discovery0=http://127.0.0.1:${DISCOVERY_PEER_PORT}"
}

function gendscv() {
	echo "Generating discovery..."
	uuid=$(uuidgen)
	curl -X PUT http://127.0.0.1:${DISCOVERY_CLIENT_PORT}/v2/keys/_etcd/registry/${uuid}/_config/size -d value=3
	echo "UUID: ${uuid}"
}

## Main
[ $# -lt 1 ] && echo "Usage: $0 cmd[etcd|discovery|gendscv]" && exit 1

cmd=$1
args=$@
extargs=$(echo ${args} | awk '{for(i=2; i<=NF; i++) { printf("%s ", $i) } }')

case ${cmd} in
    etcd)
        etcd $extargs
        ;;
    discovery)
        discovery $extargs
        ;;
    gendscv)
        gendscv $extargs
        ;;
    *)
        echo "Unknown command: \"${cmd}\""
        ;;
esac



