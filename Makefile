# This is a GNU Makefile

# This Makefile is part of the Alitheia system produced by the SQO-OSS
# consortium and is covered by the same LICENSE as the rest of the system:
# the 2-Clause FreeBSD license which you may find in the LICENSE file.

# This top-level Makefile allows you to build the Alitheia system (the
# core system, also sometimes called the cruncher) with either Maven
# or with Make. The latter is much much faster, but might miss stuff.
# The Make system is the default; to switch on Maven builds, set
# WITH_MAVEN to a non-empty value, for instance like so:
#	make build install run WITH_MAVEN=YES
#
# The top-level targets are the following:
#
# build		- Compile all of the Java sources into jars for each bundle.
# install	- Install the resulting bundles into the equinox dir.
# run		- Run the OSGi / Equinox system.
# run-bg	- Run the OSGi / Equinox system without a console and in
#             	background mode.
# run-ui	- Start up tomcat with the public-facing web front end.
# stop-ui	- Stop the tomcat instance.
# start		- Run the web UI and the core system.
# start-bg	- Run the web UI and the core system (the latest without a
#             console and in BG mode).
# clean		- Remove all build artifacts and logs.
# clean-log	- Clean up just the logs. Keeps show-log short.
# show-log      - Finds the run log and prints it.
#
# drop-db	 - Remove the DB, so it will be re-created next run.
# show-db	 - Start the database CLI for database manipulation.
# fill-db        - install canned data to the database
# clean-db       - clear the database using delete statements; this keeps
#		    the structure (unlike drop-db).
# show-db-tables - shows the generated database tables.
#
# eclipse-up-branch  - Update the eclipse branch from the current workdir
# eclipse-up-workdir - Update the current workdir from the eclipse branch
#
# After 'make run' you may need to type 'close' on the OSGi console
# to quit the Alitheia system (in production circumstances you would
# not have the console). The run target assumes (and does not check)
# that you have done a 'make install' already.

###
#
# Sensible user-configuration settings. Other things don't make too much
# sense to change (below this section) unless you are doing actual
# build-system hacking.
#

# This is where OSGi / Equinox is installed under this directory.
PREFIX=equinox
# Use Derby? If set to anything other than "YES", we assume Postgres.
DB_DERBY?=YES

#
# END OF USER CONFIGURATION AREA
#
###


###
#
# Non-configurable stuff.
#
# Subdirectories to build or install from.
SUBDIRS=sharedlibs \
	alitheia \
	corba \
	metrics
NOBUILD_SUBDIRS=extlibs

CLASSPATH_JARS=$(strip $(shell find extlibs -name '*.jar' -type f ; find equinox -name '*.jar' -type f))
$(foreach d,$(CLASSPATH_JARS),$(eval CLASSPATH:=$(d):$(CLASSPATH)))
ifeq ($(OS),Windows_NT)
CLASSPATH:=$(subst :,;,$(subst /,\,$(CLASSPATH)))
endif


TOP_SRCDIR=$(shell pwd)
ABS_PREFIX=$(shell cd $(PREFIX) && pwd)

LOG4J_PREFIX=$(ABS_PREFIX)
ifeq ($(OS),Windows_NT)
LOG4J_PREFIX:=$(subst /cygdrive/c,,$(LOG4J_PREFIX))
endif

all : build

notify :
	@echo "# Entering top-level."

include Makefile.common

$(foreach d,$(SUBDIRS),$(eval $(call subdir_template,build,$(d),all)))
$(foreach d,$(SUBDIRS) $(NOBUILD_SUBDIRS),$(eval $(call subdir_template,clean,$(d),clean)))
$(foreach d,$(SUBDIRS) $(NOBUILD_SUBDIRS),$(eval $(call subdir_template,install,$(d),install)))



build : notify $(foreach d,$(SUBDIRS),build-$(d) install-$(d))

install : $(foreach d,$(SUBDIRS) $(NOBUILD_SUBDIRS),install-$(d))
	rm -Rf ${PREFIX}/configuration/org.eclipse.osgi
	rm -f ${PREFIX}/configuration/*.log

clean : clean-log $(foreach d,$(SUBDIRS) $(NOBUILD_SUBDIRS),clean-$(d))
	rm -rf $(PREFIX)/configuration/org.eclipse.osgi
	rm -f $(PREFIX)/eu.sqooss.service.*.jar \
		$(PREFIX)/eu.sqooss.metrics.*.jar
	rm -f $(PREFIX)/*.jar
	rm -rf doc/javadoc

clean-log :
	rm -f $(PREFIX)/alitheia.log $(PREFIX)/hibernate.log $(PREFIX)/derby.log
	rm -f $(PREFIX)/logs/*
	rm -f $(PREFIX)/configuration/*.log

distclean: clean clean-log clean-db
	-find . -type f|grep *~|xargs rm
	-find . -type f|grep DS_Store|xargs rm 

javadoc:
	ALLSRC=`find . -type f -name "*.java"|tr '\n' ' '` && javadoc -d doc/javadoc -classpath $(CLASSPATH) $$ALLSRC

#Just a dummy config file
CONFIG=-Xmx512M 

DEBUGOPT=-Xdebug -Xrunjdwp:transport=dt_socket,server=y,address=8000,suspend=y 

CL_CONFIG=-Dorg.apache.commons.logging.Log=org.apache.commons.logging.impl.Log4JLogger
LOG4J_CONFIG=-Dlog4j.configuration=file://$(LOG4J_PREFIX)/configuration/log4j.properties
JETTY_CONFIG=-DDEBUG_VERBOSE=1 -DDEBUG_PATTERNS=main,org.mortbay.http -Dorg.mortbay.log.LogFactory.noDiscovery=false

HIBERNATE_CONFIG=-Deu.sqooss.hibernate.config=hibernate.cfg.xml

# $(CONFIG) would typically be used to set system properties.
run :
	cd $(PREFIX) && \
	java $(CONFIG) \
		-DDEBUG $(CL_CONFIG) $(LOG4J_CONFIG) $(JETTY_CONFIG) \
		$(HIBERNATE_CONFIG) \
		-jar org.eclipse.osgi_3.3.0.v20070321.jar -console

run-bg :
	cd $(PREFIX) && \
	java $(CONFIG) \
		-DDEBUG $(CL_CONFIG) $(LOG4J_CONFIG) $(JETTY_CONFIG) \
		-jar org.eclipse.osgi_3.3.0.v20070321.jar \
		-no-exit &

debug :
	cd $(PREFIX) && \
	java $(DEBUGOPT) $(CONFIG) \
		-DDEBUG $(CL_CONFIG) $(LOG4J_CONFIG) $(JETTY_CONFIG) \
		-jar org.eclipse.osgi_3.3.0.v20070321.jar -console 

run-ui :
	cd ui/webui && $(MAKE) start

stop-ui :
	cd ui/webui && $(MAKE) stop

start : run-ui run

start-bg : run-ui run-bg


# The default log4j configuration puts the log directly in $(PREFIX) and
# the SQO-OSS logger puts it in the bundle data directory. Handle both.
show-log :
	cat $(PREFIX)/logs/alitheia.log

# The Derby jars live underneath here in extlibs
DBPATH=extlibs/org.apache.derby_10.3.2.1
# This is the classpath needed to run Derby applications
RUN_DERBY_CLASSPATH=$(DBPATH)/derby.jar:$(DBPATH)/../org.apache.derby.tools-10.3.1.4.jar
ifeq ($(OS),Windows_NT)
RUN_DERBY_CLASSPATH:=$(subst :,;,$(subst /,\,$(RUN_DERBY_CLASSPATH)))
endif
# Command to run Derby's command-line tool ij
RUN_DERBY_IJ=java \
	-Dij.protocol=jdbc:derby: \
	-Dij.database=equinox/derbyDB \
	-cp "$(RUN_DERBY_CLASSPATH)" org.apache.derby.tools.ij

# Alternate command to run Postgres command-line tool psql
RUN_POSTGRES=psql alitheia -U alitheia

# All the db-related targets are distinguished between Derby and Postgres
ifeq ($(DB_DERBY),YES)
show-db :
	${RUN_DERBY_IJ}

show-db-tables :
	echo "show tables;" | $(RUN_DERBY_IJ) | grep "^ALITHEIA"

fill-db :
	cat examples/db-derby.sql | $(RUN_DERBY_IJ)

clean-db :
	cat examples/clear-db-derby.sql|$(RUN_DERBY_IJ)

drop-db:
	rm -rf $(PREFIX)/derbyDB
else
show-db :
	$(RUN_POSTGRES)

show-db-tables :
	echo "\dt" | $(RUN_POSTGRES)

fill-db :
	cat examples/db-psql.sql | $(RUN_POSTGRES)

clean-db :
	cat examples/clear-db-psql.sql | $(RUN_POSTGRES)

drop-db:
	echo "drop database alitheia" | $(RUN_POSTGRES)
endif




ECLIPSEDIR=$(TOP_SRCDIR)/../branches/eclipse

eclipse-up-branch: distclean
	@if [ !  "`svn status|grep -v "^?"`" ]; then \
		echo **Modified exist, take care of them first;\
		exit ; \
	fi &&\
	files=`svn st|grep ^?|tr -s ' '|cut -f2 -d' '|tr '\n' ' '` && \
	for file in $$files; do \
		mkdir -p ../tmp/`dirname $$file`;  \
		cp -rv $$file ../tmp/$$file;  \
	done && \
	rsync -rv ../tmp/ $(ECLIPSEDIR) && \
	echo Cleaning up.... && \
	rm -R ../tmp &&\
	echo "#################################" && \
	echo "#Commit the following files/dirs#" && \
	echo "#################################" && \
	svn st $(ECLIPSEDIR) 

eclipse-up-workdir: distclean
	if [ ! -z "`svn status|grep -v "^?"`" ]; then \
		echo Modified or added files are in place, take care of them first;\
		exit ; \
	fi && \
	(cd $(ECLIPSEDIR) && svn up) && \ 
	rsync -rv --exclude ".svn" $(ECLIPSEDIR)/ $(TOP_SRCDIR)/ 


test-updater : test-start-follower test-updater-step1 test-updater-step2 test-stop-follower

test-updater-step1:
	$(MAKE) test-clean-yoyo
	$(MAKE) test-checkout-yoyo test-hit-updater

test-updater-step2: 
	$(MAKE) test-commit-yoyo 
	$(MAKE) test-hit-updater test-clean-yoyo 

test-checkout-yoyo :
	-mkdir -p examples/tmp/
	test -d examples/tmp/
	svn co https://cvs.codeyard.net/svn/yoyo  examples/tmp/yoyo

test-commit-yoyo :
	for i in 1 2 3 ; do date >> examples/tmp/yoyo/README.txt ; sleep `jot -r 1 1 10` ; M=`perl ../../tools/tagline.pl` ; svn commit -m "$$M" examples/tmp/yoyo/README.txt ; done

test-clean-yoyo :
	rm -rf examples/tmp/yoyo
	rm -f examples/tmp/yoyo.pid

test-hit-updater :
	curl 'http://localhost:8088/updater?target=code&project=SQO-OSS'

test-start-follower :
	tail -f equinox/logs/alitheia.log & echo $$! > examples/tmp/yoyo.pid

test-stop-follower :
	kill `cat examples/tmp/yoyo.pid`

