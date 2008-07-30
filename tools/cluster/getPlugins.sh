#!/bin/sh
. ./config
scriptname=`basename $0`
if [ "$1" = "" ]; then
  echo "
***************************
* SQO-OSS - $scriptname
***************************
Description: Fetch a comma seperated list 
             of installed / registered plugins (id,pluginName,hash)

Syntax: $scriptname <server:port>

Example: $scriptname localhost:8088
  "
  exit 1
fi
server=$1

$curl -s  http://$server/index | $sed $sed_args -n -f $scripts/getPlugins.sed 
