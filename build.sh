#!/usr/bin/env bash

#build
cd api/webapp
npm run-script build
#cd ..
#./gradlew build
#docker build . -t lunchometer


#docker run -d -p 8080:8080 --name lunchometer lunchometer