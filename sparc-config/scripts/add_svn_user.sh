#!/usr/bin/bash

# Add user script for SQO-OSS SVN repository
# 
# Author: Vassilios Karakoidas (bkarak@aueb.gr)
# $Id $

# external programs
HTPASSWD=/usr/apache2/bin/htpasswd
SVNAUTH=/var/svn/common/conf/svnauth
SVNAUTHDESC=/var/svn/common/conf/svnauthdesc

# init variables
USERNAME="N/A"
REALNAME="N/A"
PASSWORD="N/A"
ORGANIZATION="N/A"
EMAIL="N/A"

#################################################
# main body 
#################################################
echo "Add user script (beta)"
echo "SQO-OSS SVN repository"

# read username
echo "Enter username:"
read USERNAME

# read realname
echo "Enter real name:"
read REALNAME

# read organization
echo "Enter Organization:"
echo "Available options->"
echo "SENSE, BD-NET, KDE, PLASE, PROSYST, SIRIUS, KDAB"
read ORGANIZATION

# read password
echo "Enter password:"
read PASSWORD

echo "Enter email:"
read EMAIL

# echo selected info so far
clear
echo "Username: $USERNAME"
echo "Real name: $REALNAME"
echo "Organization: $ORGANIZATION"
echo "Password: $PASSWORD"
echo "Email: $EMAIL"
echo "Create new account (y/n)?"
read CREATE_ANSWER

if [ $CREATE_ANSWER = "y" ]
then
	$HTPASSWD -bm $SVNAUTH $USERNAME $PASSWORD
	echo "$USERNAME:$REALNAME:$ORGANIZATION:$EMAIL" >> $SVNAUTHDESC
	echo "User $USERNAME created"
else
	echo "Canceled ... exiting"
fi
