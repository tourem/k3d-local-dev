k3d cluster create devcluster --servers 1 --agents 2 --api-port 127.0.0.1:6445 --k3s-arg "--disable=traefik@server:0" \
-p "8092:30040@agent:0" -p "8082:30030@agent:0" -p "8081:30090@agent:0" -p "443:30091@agent:0" -p "8200:30080@agent:0" \
-p "27017:30070@agent:0"  -p "5432:30060@agent:0" -p "9021:30100@agent:0" \
--agents-memory 3G --servers-memory 6G --k3s-arg '--kubelet-arg=eviction-hard=imagefs.available<1%,nodefs.available<1%@server:0' \
--k3s-arg '--kubelet-arg=eviction-minimum-reclaim=imagefs.available=1%,nodefs.available=1%@server:0' \
--k3s-arg '--kubelet-arg=eviction-hard=imagefs.available<1%,nodefs.available<1%@agent:0,1' \
--k3s-arg '--kubelet-arg=eviction-minimum-reclaim=imagefs.available=1%,nodefs.available=1%@agent:0,1' \
--registry-use k3d-dev-app-registry:5050 --registry-config local-registry.yaml \
--wait

#k3d cluster create devcluster --k3s-arg '--pause-image=rancher/mirrored-pause:3.6@all:*' --k3s-arg '--disable=metrics-server@all:*' --k3s-arg '--disable=traefik@all:*' --servers 1 --agents 2 --api-port 127.0.0.1:6445 --volume /Users/mtoure/dev/zephyr/local-dev/mongo/data:/tmp/db/data --volume /Users/mtoure/dev/zephyr/local-dev/mongo/config:/tmp/db/config --volume /Users/mtoure/dev/zephyr/local-dev/vault/data:/tmp/vault/data --registry-use k3d-dev-app-registry:5050 --registry-config /Users/mtoure/k3d/dev-registries.yaml --wait

#k3d image import rancher/mirrored-pause:3.6 -c devcluster

#sleep 15

#kubectl -n kube-system set image deployment/coredns coredns=k3d-dev-app-registry:5050/rancher/mirrored-coredns-coredns:1.9.1
#kubectl -n kube-system set image deployment/local-path-provisioner local-path-provisioner=k3d-dev-app-registry:5050/rancher/local-path-provisioner:v0.0.21
#kubectl -n kube-system set image deployment/metrics-server metrics-server=k3d-dev-app-registry:5050/rancher/mirrored-metrics-server:v0.5.2

#sleep 15
