red=`tput setaf 1`
green=`tput setaf 2`
reset=`tput sgr0`
 
display_usage() {
        echo 'Veuillez passer votre uid ldap en paramètre : ./$0 uid'
        return;
}
 
# check whether user had supplied -h or --help . If yes display usage
if [[ ( $# == "--help") ||  $# == "-h" ]]
then
        display_usage
        exit 0
fi
 
if [[ $# -lt 1 ]] ; then
    display_usage
    return;
fi
 
export id=$1
read -p 'Votre mot de passe ldap :' -s password
#Escape special characters in your password
password=$(echo $password | perl -MURI::Escape -ne 'chomp;print uri_escape($_),"\n"')
#Set proxy on environnement variable
export https_proxy="$1:${password}@proxy"
#Set proxy on environnement variable
export http_proxy="$1:${password}@proxy"

# installation via brew. PS : les composants k3d sont compatibles avec la version 5.4.6
brew install k3d
brew install helmfile 
# Octant is a tool for developers to understand how applications run on a Kubernetes cluster : https://github.com/vmware-tanzu/octant
brew install octant
 
# Connexion à la registry dogen
docker login -u $id -p $password docker.artifactory-dogen.group.echonet

password=""

k3d_version="$(k3d   --version | sed -n 's/^k3d version v//p;')"
k3s_version="$(k3d   --version | sed -n 's/^k3s version \(.*\) .*$/\1/p;')"

cat << EOF > $HOME/dev-registries.yaml
mirrors:
  "localhost:5050":
    endpoint:
      - http://k3d-dev-app-registry:5050/
EOF

echo 'Installations des images composants k3d'
# pull des images docker nécessaires pour le fonctionnement de k3d
k3d_images=(
        "ghcr.io/k3d-io/k3d-proxy:${k3d_version}"
        "rancher/k3s:${k3s_version}"
        "ghcr.io/k3d-io/k3d-tools:${k3d_version}"
        "rancher/mirrored-pause:3.6"
        "rancher/local-path-provisioner:v0.0.21"
        "rancher/mirrored-coredns-coredns:1.9.1"
        "rancher/klipper-helm:v0.7.3-build20220613"
        "rancher/mirrored-metrics-server:v0.5.2"
        "registry:2"
)
for img in ${k3d_images[@]}; do
        docker pull "docker.artifactory-dogen.group.echonet/${img}"
        docker tag  "docker.artifactory-dogen.group.echonet/${img}" "${img}"
        docker rmi  "docker.artifactory-dogen.group.echonet/${img}"
done
echo 'Fin Pull des images composants k3d'


# création de la registry locale et imoort des images
echo 'Création registry locale'


k3d registry create dev-app-registry --port 5050

echo 'Pull images : mongodb, postgreSQL, vault'

tools_images=(
        "mongo:4.2.6"
        "hashicorp/vault-k8s:1.1.0"
        "hashicorp/vault:1.11.6"
        "bitnami/bitnami-shell:11-debian-11-r50"
        "bitnami/postgresql:15.1.0-debian-11-r0"
        "httpd:2.4.6-4"
)
for img in ${tools_images[@]}; do
        docker pull "docker.artifactory-dogen.group.echonet/${img}"
        docker tag  "docker.artifactory-dogen.group.echonet/${img}" "${img}"
        docker rmi  "docker.artifactory-dogen.group.echonet/${img}"
done

echo 'Fin Pull images : mongodb, postgreSQL, vault'


# création de la registry locale et imoort des images

echo 'Création registry locale'

echo 'Import images --> registry locale : localhost:5050'

for img in ${k3d_images[@]} ${tools_images[@]}; do
        docker tag      "${img}"        "localhost:5050/${img}"
        docker push                     "localhost:5050/${img}"
        docker rmi                      "localhost:5050/${img}"
done
