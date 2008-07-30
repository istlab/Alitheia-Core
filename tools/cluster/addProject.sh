#!/bin/sh
. ./config

if [ "$2" = "" ]; then
  echo "
***************************
* SQO-OSS - $scriptname
***************************
Add a project on a given server 
You should already have initialized the project by initProject.sh script!
NOTE: Project will still be visible by all attached servers

Syntax: $scriptname <server:port> <project name>
 
Example: $scriptname localhost:8088 iTALC 
  "
  exit 1
fi
server=$1;
projectName=$2;

infoFile="$data_home/$projectName/info.txt"

if [ ! -f $infoFile ]; then
 echo "Couldn't find the information file $infoFile"
 exit 1
fi

echo "================"
echo "# submiting $infoFile "
echo "================"
$curl -q -d"info=$infoFile" http://$server/diraddproject 

