#!/bin/bash 

#Construct the program's classpath
CP=`find lib -type f -name '*.jar'|while read file; do echo $file; done | tr '\n' ':'`

java -classpath $CP:$CLASSPATH:bin eu.sqooss.vcs.CmdLine $@