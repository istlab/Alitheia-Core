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
	extlibs/javax.servlet_2.4.0.v200612120446.jar
install : $(PREFIX)/bin
	for i in $(BUNDLES) ; do cp $$i/target/*.jar $(PREFIX)/bin/ ; done
	for i in $(LIBS) ; do cp $$i $(PREFIX)/bin/ ; done

install-libs :
	mvn install:install-file -DgroupId=javax -DartifactId=servlet \
		-Dversion=2.4.0.v200612120446 -Dpackaging=jar \
		-Dfile=extlibs/javax.servlet_2.4.0.v200612120446.jar 
	mvn install:install-file -DgroupId=org.eclipse.equinox \
		-DartifactId=osgi \
		-Dversion=3.3.0 -Dpackaging=jar \
		-Dfile=extlibs/org/eclipse/equinox/osgi/3.3.0/osgi-3.3.0.jar
