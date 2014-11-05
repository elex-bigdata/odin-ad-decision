#!/bin/bash
git pull
mvn clean package -DskipTests

#cp war to tomcat