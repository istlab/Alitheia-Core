#!/bin/bash

#Construct the program's classpath
CP=`find lib -type f -name '*.jar'|while read file; do echo $file; done | tr '\n' ':'`
SQOOSS_JAR="dist/sqo-oss.jar"

java -classpath $SQOOSS_JAR:$CP:$CLASSPATH eu.sqooss.tool.Main $@