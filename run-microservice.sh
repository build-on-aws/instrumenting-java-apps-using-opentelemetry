#!/bin/bash

mvn clean package -Dmaven.test.skip=true

java -jar target/hello-app-1.0.jar
