#!/bin/bash
git pull
mvn clean package -DskipTests

#cp war to tomcat
cp target/odin-ad-decision.war /home/elex/catalina/apache-tomcat-7.0.56.odin/webapps/