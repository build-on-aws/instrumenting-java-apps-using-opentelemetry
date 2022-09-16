#!/bin/bash

mvn clean package -Dmaven.test.skip=true

java -javaagent:./${AGENT_FILE} -jar target/hello-app-1.0.jar
