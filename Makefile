PREFIX?=.

all :
	@echo "# You need Maven2 installed and should run make for one of:"
	@echo "#     build"

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
OSGI=extlibs/org/eclipse/equinox/osgi/3.3.0/
install : $(PREFIX)/bin
	for i in $(BUNDLES) ; do cp $$i/target/*.jar $(PREFIX)/bin/ ; done
	cp $(OSGI)/*.jar $(PREFIX)/bin/

