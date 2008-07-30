#!/bin/sh
. ./config
scriptname=`basename $0`
if [ "$3" = "" ]; then
  echo "
***************************
* SQO-OSS - $scriptname
***************************
Description: Synchronize plugin on a projectID at a given server

Syntax: $scriptname <server:port> <projectID> <plugin hash>

 
Example: $scriptname localhost:8088 1 c537b3886b7964b49e4d8f37b129ecf6
  "
  exit 1
fi
server=$1
projectID=$2
pluginHash=$3


$curl -s -q -d"reqParSyncPlugin=$pluginHash&projectId=$projectID" "http://$server/projects" 


exit


