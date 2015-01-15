#!/bin/bash
git checkout redis
git pull

#clean the cache
rm -rf web/WEB-INF/classes/*

mvn clean package -Pdev -DskipTests

thorpid=`ps aux | grep  '/home/elex/webserver/apache-tomcat-7.0.56-test' |grep -v grep | awk '{print $2}'`

echo "kill thor thorpid"
kill -9 $thorpid

#cp war to tomcat
rm -rf /home/elex/webserver/apache-tomcat-7.0.56-test/work/*
rm -rf /home/elex/webserver/apache-tomcat-7.0.56-test/webapps/thor
cp target/thor.war /home/elex/webserver/apache-tomcat-7.0.56-test/webapps/

/home/elex/webserver/apache-tomcat-7.0.56-test/bin/startup.sh