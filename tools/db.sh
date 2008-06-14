#! /bin/sh

# Copyright (C) 2008 by the members of the SQO-OSS consortium <info@sqo-oss.eu>
# Copyright (C) 2008 by Adriaan de Groot <groot@kde.org>
#
# This file is released under a 2-clause BSD license; see the file
# LICENSE in the SQO-OSS release.

# Script to do some basic database manipulations, from the older
# 'make *-db' targets. It handles both Derby and Postgres, but
# only in the default locations.
#
# You can select a database to use in one of two ways: set
# the environment variable DB to either 'derby' or 'postgres'
# or use the command-line arguments --with-derby or --with-postgres.
# If you use Postgres, you may want to set POSTGRES_COMMAND
# in the environment to the command (including parameters)
# to start up the Postgres command-line client psql.
#
# Usage:
#
#	db.sh [--with-derby|--with-postgres] cmd
#
# Where cmd is one of:
#	<blank> or show	- just start the command-prompt for the db
#	tables		- display the tables in the db
#	fill		- fill db with default data
#	clean		- clean the db but preserve tables
#	drop		- drop as much as possible of the db


test -d "sqoossrepo" || cd ..
test -d "sqoossrepo" || { echo "! Call this script in devel/trunk or devel/trunk/tools." ; exit 1 ; }

db=$DB
test "x--with-derby" = "x$1" && { db=derby ; shift ; }
test "x--with-postgres" = "x$1" && { db=postgres ; shift ; }

DERBY_JAR=sqoossrepo/org/apache/derby/10.3.2.1/derby-10.3.2.1.jar
DERBY_TOOLS_JAR=sqoossrepo/org/apache/derby/tools/10.3.1.4/tools-10.3.1.4.jar
DERBY_COMMAND='java -Dij.protocol=jdbc:derby: -Dij.database=equinox/derbyDB -cp "$DERBY_JAR:$DERBY_TOOLS_JAR" org.apache.derby.tools.ij'

test -z "$POSTGRES_COMMAND" && POSTGRES_COMMAND="psql alitheia -U alitheia"

case "$db" in
derby ) 
	echo "# Derby DB selected"
	db_show() {
		eval $DERBY_COMMAND
	}
	db_tables() {
		echo "show tables;" | eval $DERBY_COMMAND | grep "^ALITHEIA"
	}
	db_fill() {
		 sed -e 's/@SCHEMA@/alitheia./' \
			-e 's/@TS=\(.*\)@/\1-12.27.38.2345/' \
			examples/db-derby.sql | eval $DERBY_COMMAND
	}
	db_clean() {
		cat examples/clear-db-derby.sql | eval $DERBY_COMMAND
	}
	db_drop() {
		rm -rf equinox/derbyDB
	}
	;;
postgres ) 
	echo "# Postgres DB selected"
	db_show() {
		$POSTGRES_COMMAND
	}
	db_tables() {
		echo "\dt" | $POSTGRES_COMMAND
	}
	db_fill() {
		sed -e 's/@SCHEMA@//' \
			-e 's/@TS=\(.*\)@/\1 12:27/' \
			 examples/db-derby.sql | $POSTGRES_COMMAND
	}
	db_clean() {
		sed 's/ACTION/delete from/' examples/drop-db-postgres.sql | $POSTGRES_COMMAND
	}
	db_drop() {
		sed 's/ACTION/drop table/' examples/drop-db-postgres.sql | $POSTGRES_COMMAND
	}
	;;
* ) echo "! Invalid database (expect derby or postgres, got <$db>)" ; exit 1 ;;
esac


CMD="$1"
test -z "$CMD" && CMD="show"

case "$CMD" in
show|tables|fill|clean|drop )
	echo "# Running database command <$CMD>."
	eval db_$CMD
	;;
* )
	echo "! Unknown command <$1>"
	exit 1
	;;
esac



