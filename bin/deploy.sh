#!/bin/bash
git pull

#clean the cache
rm -rf web/WEB-INF/classes/*

mvn clean package -DskipTests

#cp war to tomcat
cp target/thor.war /home/elex/webserver/apache-tomcat-7.0.56-thor/webapps/