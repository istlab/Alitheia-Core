PREFIX?=.

all :
	@echo "# You need Maven2 installed and should run make for one of:"
	@echo "#     build install"
	@echo "# Set PREFIX to the directory to install to (default './')."
	@echo "#"
	@echo "# Other targets:"
	@echo "#     install-libs: work around downloading needed libs."

build : build-alitheia build-metrics

build-alitheia :
	( cd alitheia && mvn package )

build-metrics :
	( cd metrics && mvn package )

$(PREFIX)/bin :
	mkdir $(PREFIX)/bin
	test -d $(PREFIX)/bin

BUNDLES=alitheia/logger \
	alitheia/messaging \
	alitheia/webui
LIBS=extlibs/org/eclipse/equinox/osgi/3.3.0/osgi-3.3.0.jar \
	extlibs/javax.servlet_2.4.0.v200612120446.jar \
	extlibs/org.eclipse.equinox.http.servlet_1.0.0.v20070318.jar \
	extlibs/org.eclipse.equinox.http_1.0.100.v20070226.jar \
	extlibs/org.eclipse.equinox.servlet.api_1.0.0.v20070226.jar \
	extlibs/org.eclipse.osgi.services_3.1.100.v20060918.jar

#	extlibs/org.eclipse.equinox.servletbridge_1.0.0.v20070322.jar
#	extlibs/org.eclipse.equinox.http.servletbridge_1.0.0.v20070318.jar
#	extlibs/org.eclipse.equinox.http.jetty_1.0.0.v20070318.jar
#	extlibs/org.eclipse.equinox.http.registry_1.0.0.v20070318.jar

install : $(PREFIX)/bin
	for i in $(BUNDLES) ; do cp $$i/target/*.jar $(PREFIX)/bin/ ; done
	for i in $(LIBS) ; do cp $$i $(PREFIX)/bin/ ; done

install-libs :
	mvn install:install-file -DgroupId=org.eclipse.equinox \
		-DartifactId=osgi \
		-Dversion=3.3.0 -Dpackaging=jar \
		-Dfile=extlibs/org/eclipse/equinox/osgi/3.3.0/osgi-3.3.0.jar
	mvn install:install-file -DgroupId=javax -DartifactId=servlet \
		-Dversion=2.4.0.v200612120446 -Dpackaging=jar \
		-Dfile=extlibs/javax.servlet_2.4.0.v200612120446.jar 
	mvn install:install-file -DgroupId=org.eclipse.equinox.http \
		-DartifactId=servlet \
		-Dversion=1.0.0.v20070318 -Dpackaging=jar \
		-Dfile=extlibs/org.eclipse.equinox.http.servlet_1.0.0.v20070318.jar
	mvn install:install-file -DgroupId=org.eclipse.equinox.servlet \
		-DartifactId=api \
		-Dversion=1.0.0.v20070226 -Dpackaging=jar \
		-Dfile=extlibs/org.eclipse.equinox.servlet.api_1.0.0.v20070226.jar
	mvn install:install-file -DgroupId=org.eclipse.equinox \
		-DartifactId=http \
		-Dversion=1.0.100.v20070226 -Dpackaging=jar \
		-Dfile=extlibs/org.eclipse.equinox.http_1.0.100.v20070226.jar
	mvn install:install-file -DgroupId=org.eclipse.osgi \
		-DartifactId=services \
		-Dversion=3.1.100.v20060918 -Dpackaging=jar \
		-Dfile=extlibs/org.eclipse.osgi.services_3.1.100.v20060918.jar

