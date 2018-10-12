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

docker-compose exec kafka1 kafka-topics --create --topic commands --partitions 1 --replication-factor 1 --if-not-exists --zookeeper zoo1:2181
docker-compose exec kafka1 kafka-topics --create --topic events --partitions 1 --replication-factor 1 --if-not-exists --zookeeper zoo1:2181
docker-compose exec kafka1 kafka-topics --create --topic commandresponses --partitions 1 --replication-factor 1 --if-not-exists --zookeeper zoo1:2181
docker-compose exec kafka1 kafka-topics --list --zookeeper zoo1:2181



