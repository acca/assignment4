#!/bin/bash
mvn clean install -f a4Client
mvn clean install -f a4Server
cp a4Server/a4Server-ear/target/a4Server-ear-1.0-SNAPSHOT.ear $JBOSS_HOME/standalone/deployments/
sleep 5
mvn exec:java -f a4Client
