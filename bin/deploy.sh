#!/bin/bash
git pull

#clean the cache
rm -rf web/WEB-INF/classes/*

mvn clean package -DskipTests

thorpid=`ps aux | grep  '/home/elex/webserver/apache-tomcat-7.0.56-thor' |grep -v grep | awk '{print $2}'`

echo "kill thor thorpid"
`kill -9 thorpid`

#cp war to tomcat
rm -rf /home/elex/webserver/apache-tomcat-7.0.56-thor/work/*
rm -rf /home/elex/webserver/apache-tomcat-7.0.56-thor/webapps/thor
cp target/thor.war /home/elex/webserver/apache-tomcat-7.0.56-thor/webapps/

`/home/elex/webserver/apache-tomcat-7.0.56-thor/bin/startup.sh`