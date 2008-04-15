#! /bin/sh

# Script to do some basic database manipulations, from the older
# 'make *-db' targets. Right now it is just for Derby.

test -d "sqoossrepo" || cd ..
test -d "sqoossrepo" || { echo "! Call this script in devel/trunk or devel/trunk/tools." ; exit 1 ; }

DERBY_JAR=sqoossrepo/org/apache/derby/10.3.2.1/derby-10.3.2.1.jar
DERBY_TOOLS_JAR=sqoossrepo/org/apache/derby/tools/10.3.1.4/tools-10.3.1.4.jar

java \
	-Dij.protocol=jdbc:derby: \
	-Dij.database=equinox/derbyDB \
	-cp "$DERBY_JAR:$DERBY_TOOLS_JAR" \
	org.apache.derby.tools.ij


