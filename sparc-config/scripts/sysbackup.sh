#!/usr/bin/bash

# sysbackup.sh
# This is the system backup script
#
# Author: Kostas Stroggylos
# $Id $

BACKUPDIR=/backup

echo "Starting backup. Please wait..."

cd $BACKUPDIR

/usr/sfw/bin/gtar --create --verbose --gzip -h --file /tmp/backup.tar.gz * >& /tmp/backup.log

echo "Transferring backup file..."
 
scp /tmp/backup.tar.gz circular@gemini.dmst.aueb.gr:./sense.backup.`date +%Y%m%d%H%M`.tar.gz

echo "Transferring backup log...."

gzip /tmp/backup.log

scp /tmp/backup.log.gz circular@gemini.dmst.aueb.gr:./sense.backup.log.`date +%Y%m%d%H%M`.gz

echo "Removing temporary files..."
rm /tmp/backup.tar.gz
rm /tmp/backup.log.gz

echo "Backup completed successfully."
