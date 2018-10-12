#!/usr/bin/env bash

#rm -rf src/main/avro/schema/*
#
#for file in ../avro/src/main/avdl/*
#do
#    filename=$(basename -- "$file")
#    java -jar avro-tools.jar idl2schemata ${file} ../avro/src/main/avro/
#done

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