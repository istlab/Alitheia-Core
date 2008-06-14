#! /bin/sh
for i in `find . -name pom.xml`
do
echo $i
xmllint --postvalid --noout --schema tools/maven-4.0.0.xsd $i
done
