myNamespace="kafka"
templateDir=$PWD
kubectl get namespace | grep -q "^$myNamespace " || kubectl create namespace $myNamespace

kubectl create -f $templateDir