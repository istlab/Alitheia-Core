#!/bin/sh
. ./config
scriptname=`basename $0`
if [ "$3" = "" ]; then
  echo "
***************************
* SQO-OSS - $scriptname
***************************
Description: Update a project on a given server

Syntax: $scriptname <server:port> <project name> <all|code|mail|bugs>

 
Example: $scriptname localhost:8088 iTALC all
  "
  exit 1
fi
server=$1
project=$2
target=$3

case $target in
 all|code|mail|bugs)
     ;;
  *) echo "Unsupported target"
     exit
	 ;;
esac

$curl -s "http://$server/updater?project=$project&target=$3" 


exit


