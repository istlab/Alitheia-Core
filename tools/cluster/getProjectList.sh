#!/bin/sh
. ./config
scriptname=`basename $0`
if [ "$1" = "" ]; then
  echo "
***************************
* SQO-OSS - $scriptname
***************************
Description: Fetch a comma seperated list 
             of installed packages (id,project_name)

Syntax: $scriptname <server:port>

Example: $scriptname localhost:8088
  "
  exit 1
fi
server=$1

$curl -s http://$server/projects | $sed $sed_args -n -f $scripts/getProjectList.sed 
