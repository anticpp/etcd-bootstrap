Bootstrap the etcd cluster with secure mutual-TLS. 

# TLS topology

Nodes/IPs:

- node0: 192.168.56.10
- node1: 192.168.56.11
- node2: 192.168.56.12

```
=========================================
Certificate signing:
    etcd-peer.pem             <- etcd-ca
    etcd.pem, etcd-client.pem <- etcd-ca

Associated etcd arguments:
Client-to-etcd-cluster:
    etcd:    --client-cert-auth 
             --trusted-ca-file 
             --cert-file 
             --key-file
    etcdctl: --cacert
             --cert
             --key
Peer-to-peer:
    etcd:    --peer-client-cert-auth
             --peer-trusted-ca-file
             --peer-cert-file
             --peer-key-file
=========================================

                -----------------<------                    ----------------                ---------------
            --->|  etcd:node0   |----  |                    |  etcd:node1  |<---            |  etcd:node2 |
            |   -----------------   |  |                    ----------------   |            ---------------
            |                       |  |                                 |     |
    <etcd.pem>	              <etcd-peer.pem>                        <etcd-peer.pem>
    <etcd-key.pem>            <etcd-peer-key.pem>                    <etcd-peer-key.pem>
            |                       |  |                                 |     |
            |                       |  ----------------------------------|     |
            |                       |-------------------------------------------
            |					                   (Peer-to-peer)
            |(Client-to-etcd-cluster)
            |
            |
            |
    <etcd-client.pem>
    <etcd-client-key.pem>
			|
     --------------
     |	etcdctl  |

```

# Create CA/certificatea

```Shell=
make ca
make certs
```

# Bootstrap

Copy these onto each node:

- `pki/`
- boostrap.sh
- Configure `bootstrap.sh`

```
sh bootstrap.sh
```

# Test

Using curl

```shell=
curl --cacert ./pki/CA_etcd/cacert.pem  --cert ./pki/certs/etcd-client.pem --key ./pki/certs/etcd-client-key.pem https://127.0.0.1:2379/v2/members
```

Using etcdctl

```
etcdctl --cacert pki/CA_etcd/cacert.pem --cert pki/certs/etcd-client.pem --key pki/certs/etcd-client-key.pem --endpoints "https://127.0.0.1:2379" member list
```
