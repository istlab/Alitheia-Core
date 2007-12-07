#!/bin/bash
#
# search for jars, add the correct prefixes
#


if [ -z $1 ]; then
    echo usage: $0 path-prefix
    exit
fi

JARDIRS="extlibs equinox"

CLASSPATH=

for dir in $JARDIRS; do
    for jar in `find $1/$dir -type f |grep .jar$`; do
	CLASSPATH=$CLASSPATH:$jar
    done
done

echo $CLASSPATH

