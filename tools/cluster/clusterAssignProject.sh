#!/bin/sh
. ./config
scriptname=`basename $0`
if [ "$2" = "" ]; then
  echo "
***************************
* SQO-OSS - $scriptname
***************************
Description: Assign a project to a given ClusterNode

Syntax: $scriptname <server:port> <project name> [ClusterNode]
        If Clusternode is empty, assign it to <server>
 
Example: $scriptname localhost:8088 iTALC sqoserver1
  "
  exit 1
fi
server=$1
projectname=$2
if [ "$3" != "" ]; then
  node="&clusternode=$3"
else
  node=""
fi

$curl -s "http://$server/clusternode?action=assign_project&projectname=$projectname$node" 

exit


