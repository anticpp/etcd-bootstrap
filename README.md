Generate self-signed tls certificates for etcd cluster.


## Topology

TODO

## Use

```
# Make CA
sudo sh makeca

# Generate all certificates
sudo sh makecerts

# Configure /etc/pki/tls/openssl.conf
# Add section [CA_etcd], [CA_etcd_peer]

