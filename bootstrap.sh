#!/bin/bash

## Configure the belowing items to your deploying machines.
TOKEN=token1
CLUSTER_STATE=new
NAME_0=node0
NAME_1=node1
NAME_2=node2
HOST_0=192.168.56.10
HOST_1=192.168.56.11
HOST_2=192.168.56.12

## On each machine, configure to your current node name and ip.
THIS_NAME=${NAME_0}
THIS_HOST=${HOST_0}


CLUSTER="${NAME_0}=http://${HOST_0}:2380,${NAME_1}=http://${HOST_1}:2380,${NAME_2}=http://${HOST_2}:2380"
CLUSTER_HTTPS="${NAME_0}=https://${HOST_0}:2380,${NAME_1}=https://${HOST_1}:2380,${NAME_2}=https://${HOST_2}:2380"

ETCD_BIN=/usr/local/bin/etcd/etcd
ETCD_ENABLE_V2=true ${ETCD_BIN} --log-level=debug --data-dir=data.etcd --name ${THIS_NAME} \
	--advertise-client-urls "https://${THIS_HOST}:2379" --listen-client-urls "https://${THIS_HOST}:2379,https://127.0.0.1:2379" \
	--client-cert-auth --trusted-ca-file=./pki/CA_etcd/cacert.pem --cert-file=./pki/certs/etcd.pem --key-file=./pki/certs/etcd-key.pem \
	--initial-advertise-peer-urls "https://${THIS_HOST}:2380" --listen-peer-urls "https://${THIS_HOST}:2380" \
	--peer-client-cert-auth --peer-trusted-ca-file=./pki/CA_etcd/cacert.pem --peer-cert-file=./pki/certs/etcd-peer.pem --peer-key-file=./pki/certs/etcd-peer-key.pem \
	--initial-cluster ${CLUSTER_HTTPS} \
	--initial-cluster-state ${CLUSTER_STATE} --initial-cluster-token ${TOKEN}
	

