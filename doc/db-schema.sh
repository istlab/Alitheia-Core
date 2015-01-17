#!/usr/bin/env bash
#To produce: place UMLGraph.jar in this directory and run
#

#if [ ! -e UmlGraph.jar ]; then
#    curl -o UmlGraph.jar http://search.maven.org/remotecontent?filepath=org/umlgraph/umlgraph/5.6/umlgraph-5.6.jar
    #echo Copy UmlGraph.jar to `pwd` and re-run
    #exit 1
#fi

cwd=`pwd`
#cp UmlGraph.jar ../alitheia/core/src/main/java/eu/sqooss/service/db
cd ../alitheia/core/src/main/java/eu/sqooss/service/db
/usr/java/jre1.8.0_25/bin/java -jar /usr/local/lib/UmlGraph.jar -attributes -private -hide "Ohloh|DAObject|FileGroup|ProjectVersionParentId|DBService|StoredProjectConfig" *.java
cat graph.dot |grep -v STATE|grep -v MASK|grep -v SCM_ROOT|grep -v DEFAULT >graph1.dot
mv graph1.dot graph.dot
dot -Tpdf graph.dot >db-schema.pdf
mv db-schema.pdf $cwd
#rm UMLGraph.jar graph.dot
cd $cwd

