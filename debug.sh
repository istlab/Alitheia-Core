#!/bin/bash

OSGI=org.eclipse.osgi_3.6.0.v20100517.jar

mkdir -p runner/bundles/configuration
cat runner/equinox/config.ini| sed -e's/bundles\///' > runner/bundles/configuration/config.ini

cd runner/bundles
java -Xdebug -Xrunjdwp:transport=dt_socket,server=y,address=8000,suspend=y -Xmx1000M $@ -jar $OSGI -console && rm -R configuration/config.ini &&  cd -

