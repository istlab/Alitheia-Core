#!/bin/sh
. ./config
scriptname=`basename $0`
if [ "$1" = "" ]; then
  echo "
***************************
* SQO-OSS - $scriptname
***************************
Description: Get the list of registered ClusterNodes

Syntax: $scriptname <server:port>

Example: $scriptname localhost:8088
  "
  exit 1
fi
server=$1

$curl -s "http://$server/clusternode?action=get_known_servers" 

exit


