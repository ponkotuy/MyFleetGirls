#!/bin/sh

sbt dist
unzip ./server/target/universal/myfleetgirlsserver-$1.zip
./myfleetgirlsserver-$1/bin/myfleetgirlsserver -J-javaagent:newrelic/newrelic.jar
