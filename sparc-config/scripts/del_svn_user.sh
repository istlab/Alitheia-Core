#!/usr/bin/bash

# Remove a user from the SQO-OSS SVN repository
# 
# Author: Vassilios Karakoidas (bkarak@aueb.gr)
# $Id: $

SVNAUTH=/var/svn/common/conf/svnauth
SVNAUTHDESC=/var/svn/common/conf/svnauthdesc
TMP=temp-file-sqo-oss

###############################################
# main body

echo "SQO-OSS SVN deluser (beta)"
echo "Enter username:"
read USERNAME

if [ `grep $USERNAME $SVNAUTH` -eq 0 ] 
then
	echo "The username $USERNAME does not exist"
	exit
fi

# remove account
grep -v $USERNAME $SVNAUTH > $TMP
mv $TMP $SVNAUTH
chown webservd $SVNAUTH
chgrp webservd $SVNAUTH
rm -f $TMP

# remove description
grep -v $USERNAME $SVNAUTHDESC > $TMP
mv $TMP SVNAUTHDESC
chown webservd $SVNAUTHDESC
chgrp webservd $SVNAUTHDESC
rm -f $TMP
