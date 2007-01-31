#!/usr/bin/bash

# Update script for private web area of SQO-OSS
#
# Author: Vassilios Karakoidas (bkarak@aueb.gr)
# $Id $

WWW_DIR=/var/apache2/sqo-oss/htdocs
USERNAME=sqooss
PASSWORD=sq0o$s

echo "Deploying SQO-OSS private web site"

#cleaning the mess in current dir
rm -rf sqo-oss-private-www

svn co --username $USERNAME --password $PASSWORD https://sense.dmst.aueb.gr/svn/sqo-oss-private-www

# clean the mess
rm -f $WWW_DIR/*.php
rm -rf $WWW_DIR/resources

# 
cp -R sqo-oss-private-www/* $WWW_DIR
chown -R webservd $WWW_DIR
chgrp -R webservd $WWW_DIR

# clean the repository
rm -rf sqo-oss-private-www

echo "Deployment complete"
