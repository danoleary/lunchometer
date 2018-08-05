#!/usr/bin/env bash

#add these to /etc/hosts
#192.168.99.100    kafka1
#192.168.99.100    kafka2
#192.168.99.100    kafka3
#192.168.99.100    zoo1
#192.168.99.100    zoo2
#192.168.99.100    zoo3
#192.168.99.100    kafka-schema-registry
#192.168.99.100    kafka-schema-registry-ui
#192.168.99.100    kafka-rest-proxy
#192.168.99.100    kafka-topics-ui
#192.168.99.100    kafka-connect-ui
#192.168.99.100    zoonavigator-web
#192.168.99.100    zoonavigator-api
#https://github.com/simplesteph/kafka-stack-docker-compose

docker-compose down

docker volume prune

rm -rf ./full-stack

docker-compose up -d

sleep 60

gradle

rm -rf src/main/avro/schema/*

for file in ../avro/src/main/avdl/*
do
    filename=$(basename -- "$file")
    java -jar avro-tools.jar idl2schemata ${file} ../avro/src/main/avro/
done

for file in ../avro/src/main/avro/*
do
    schema=`cat $file`

    formattedSchema=${schema//\"/\\\"}

    body={\"schema\":\"$formattedSchema\"}

    nospacebody=${body//[[:space:]]/}

    filename=$(basename -- "$file")
    filename="${filename%.*}"

    curl -X POST -H "Content-Type: application/vnd.schemaregistry.v1+json" \
        --data "$nospacebody" \
        http://localhost:8081/subjects/$filename/versions
done

cd ..
gradle -q avro:build
cd ./kafka-setup

curl -X GET -i http://localhost:8081/subjects

docker-compose exec kafka1 kafka-topics --create --topic commands --partitions 1 --replication-factor 1 --if-not-exists --zookeeper zoo1:2181
docker-compose exec kafka1 kafka-topics --create --topic events --partitions 1 --replication-factor 1 --if-not-exists --zookeeper zoo1:2181
docker-compose exec kafka1 kafka-topics --create --topic commandResponses --partitions 1 --replication-factor 1 --if-not-exists --zookeeper zoo1:2181
docker-compose exec kafka1 kafka-topics --list --zookeeper zoo1:2181



