helm install poc-mongo-stable ./ -f values.yaml --debug --dry-run

helm repo add stable https://kubernetes-charts.storage.googleapis.com/
helm repo update
helm pull stable/chartmuseum --untar # optionally untar
helm pull bitnami/schema-registry --untar 
helm pull hashicorp/vault --untar 
helm search repo my-repo/mongodb --versions
helm pull my-repo/mongodb  --version 11.2.0 --untar


winpty kubectl exec -i -t schema-registry-64f5dcb4bd-w22jm "--" sh -c "clear; (bash || ash || sh)"
kubectl exec -ti $(kubectl get pods --selector "io.kompose.service=application-client" --output=name -n  kafka) -c ap26553-rds-poc-postgres-dev-stable -- env "TERM=xterm bash" sh -c "clear; (bash || ash || sh)"
kubectl exec -ti $(kubectl get pods --selector "io.kompose.service=application-client" --output=name -n  kafka) -- sh -c "clear; (bash || ash || sh)"

export NAMESPACE_VAULT=/
export CLUSTER=k8s
export NAMESPACE_K8S=my-ns
export ROLE=$role_default
                                                                                                                                                      
KUBE_TOKEN=$(cat /var/run/secrets/kubernetes.io/serviceaccount/token);
VAULT_K8S_LOGIN=$(curl -k -X POST -H "X-Vault-Namespace: $NAMESPACE_VAULT" -H "X-Vault-Request: true" -d '{"jwt": "'"$KUBE_TOKEN"'", "role": "'"$ROLE"'"}' https://vault.host/v1/auth/kubernetes_$CLUSTER/login);
X_VAULT_TOKEN=$(echo $VAULT_K8S_LOGIN | grep -Eo '"auth"[^,]*' |  grep -Eo '"client_token"[^,]*'  | grep -Eo '[^:]*$' | tr -d '"'); 
# récupération de secrets associés au cos : access key, secret key ...

 --namespace ingress-nginx --create-namespace