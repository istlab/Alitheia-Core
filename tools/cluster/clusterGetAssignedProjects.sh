#!/bin/sh
. ./config
scriptname=`basename $0`
if [ "$1" = "" ]; then
  echo "
***************************
* SQO-OSS - $scriptname
***************************
Description: Get a list of assigned project to a given ClusterNode

Syntax: $scriptname <server:port> [ClusterNode]
        If Clusternode is empty, get the projects assigned to <server>
 
Example: $scriptname localhost:8088 sqoserver1
  "
  exit 1
fi
server=$1
if [ "$2" != "" ]; then
  node="&clusternode=$2"
else
  node=""
fi

$curl -s "http://$server/clusternode?action=get_assigned_projects&$node" 

exit


