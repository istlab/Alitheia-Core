all : build-alitheia build-metrics

build-alitheia :
	( cd alitheia && mvn package )

build-metrics :
	( cd metrics && mvn package )

BUNDLES=alitheia/logger \
	alitheia/messaging \
	alitheia/webui
PREFIX=equinox

install :
	T="" ; \
	for i in $(BUNDLES) ; do \
		for j in $$i/target/*.jar ; do \
			if test -f $$j ; then \
				T="$T ,"`basename $$j`"@start" ; \
				cp $$j $(PREFIX) ; \
			fi ; \
		done ; \
	done ; \
	sed "s/@@ALITHEIA@@/$$T/" < $(PREFIX)/configuration/config.ini.in > $(PREFIX)/configuration/config.ini

run :
	cd $(PREFIX) && \
	java -jar org.eclipse.osgi_3.3.0.v20070321.jar -console
