#!/usr/bin/env bash
#To produce: place UMLGraph.jar in this directory and run
#

if [ ! -e UMLGraph.jar ]; then
    echo Copy UMLGraph.jar to `pwd` and re-run
    exit 1
fi

cwd=`pwd`
cp UMLGraph.jar ../alitheia/core/src/main/java/eu/sqooss/service/db
cd ../alitheia/core/src/main/java/eu/sqooss/service/db
java -jar UmlGraph.jar -attributes -private -hide "Ohloh|DAObject|FileGroup|ProjectVersionParentId|DBService|StoredProjectConfig" *.java
cat graph.dot |grep -v STATE|grep -v MASK|grep -v SCM_ROOT|grep -v DEFAULT >graph1.dot
mv graph1.dot graph.dot
dot -Tpdf graph.dot >db-schema.pdf
mv db-schema.pdf $cwd
rm UMLGraph.jar graph.dot
cd $cwd

