#!/bin/bash
# Build script
set -o pipefail
build_tag=$1
name=druid-proxy-api
node=$2
org=$3

docker build -f ./Dockerfile.Build -t ${org}/${name}:${build_tag}-build . 
docker run --name=${name}-${build_tag}-build ${org}/${name}:${build_tag}-build 
containerid=$(docker ps -aqf "name=${name}-${build_tag}-build")
docker cp $containerid:/opt/druid-proxy-api.zip druid-proxy-api.zip
docker rm $containerid
docker build -f ./Dockerfile -t ${org}/${name}:${build_tag} .
echo {\"image_name\" : \"${name}\", \"image_tag\" : \"${build_tag}\", \"node_name\" : \"$node\"} > metadata.json