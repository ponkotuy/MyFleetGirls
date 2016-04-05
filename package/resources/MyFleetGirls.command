#!/bin/sh

cd "`dirname "$0"`"
java -jar update.jar
java -jar MyFleetGirls.jar
