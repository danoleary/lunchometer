#!/usr/bin/env bash

#build
cd webapp
npm run-script build
cd ..
./gradlew build
docker build . -t lunchometer


#run
mkdir ~/mongodata
sudo docker run -d -p 27017:27017 -v ~/mongodata:/data/db mongo

docker run -d -p 8080:8080 --name lunchometer lunchometer