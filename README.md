Bootstrap an etcd cluster with `discovery protocol`, along mTLS with peer-to-peer and client-to-cluster communications.

# Newtork/TLS topology

The workflow demostrates with 3 nodes as below:

- node0/192.168.56.10
- node1/192.168.56.11
- node2/192.168.56.12

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

# Preconfigure

`makecert`: `IPs`        - Configure it to your cluster IPs
`boot`: `DISCOVERY_HOST` - Configure it to your discovery service host

# Create CA/certificatea

On node0:

```shell=
make ca
```

And complete the `CA` configurations following instructions printed, then

```Shell=
make certs
```

All certificates will be generated in directory `pki/`.

# Start discovery service

On `node0`: 

```
sh boot discovery
```

# Create discovery TOKEN

On `node0`:

```
sh boot gendscv
```

Then save the printed `UUID`, this will be used as TOKEN in the next steps.

# Bootstrap etcd

Copy files to node0,node1,node2:

- `pki/`
- boot

On each node:

```
sh boot etcd node0 192.168.56.10 <TOKEN> # node0
sh boot etcd node1 192.168.56.11 <TOKEN> # node1
sh boot etcd node2 192.168.56.12 <TOKEN> # node2
```

# Test cluster

Using curl

```shell=
curl --cacert ./pki/CA_etcd/cacert.pem  --cert ./pki/certs/etcd-client.pem --key ./pki/certs/etcd-client-key.pem https://127.0.0.1:2379/v2/members
```

Using etcdctl

```
etcdctl --cacert pki/CA_etcd/cacert.pem --cert pki/certs/etcd-client.pem --key pki/certs/etcd-client-key.pem --endpoints "https://127.0.0.1:2379" member list
```
