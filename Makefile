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
# run-ui	- Start up tomcat with the public-facing web front end.
# stop-ui	- Stop the tomcat instance.
# start		- Run the web UI and the core system.
# clean		- Remove all build artifacts and logs.
# clean-log	- Clean up just the logs. Keeps show-log short.
# clean-db	- Remove the Derby DB, so it will be re-created next run.
#		  Only useful if you are using Derby, which is the fallback
#		  when Postgres can't be found.
# show-log	- Finds the run log and prints it.
# show-db	- Start the Derby CLI for database manipulation.
#		  Only useful if you are using Derby, which is the fallback
#		  when Postgres can't be found.
#
# After 'make run' you may need to type 'close' on the OSGi console
# to quit the Alitheia system (in production circumstances you would
# not have the console). The run target assumes (and does not check)
# that you have done a 'make install' already.

# This is where OSGi / Equinox is installed under this directory.
PREFIX=equinox

# Subdirectories to build or install from.
SUBDIRS=alitheia metrics

#
# END OF USER CONFIGURATION AREA
#
###

TOP_SRCDIR=$(shell pwd)
ABS_PREFIX=$(shell cd $(PREFIX) && pwd)

all : build


# Template to carry a target to a subdirectory while preserving the
# PREFIX and Maven attributes.
define subdir_template
$(1)-$(2) :
	cd $(2) && $(MAKE) $(1) TOP_SRCDIR=$(TOP_SRCDIR) PREFIX=$(ABS_PREFIX) WITH_MAVEN=$(WITH_MAVEN)
endef

$(foreach d,$(SUBDIRS),$(eval $(call subdir_template,build,$(d))))
$(foreach d,$(SUBDIRS),$(eval $(call subdir_template,clean,$(d))))
$(foreach d,$(SUBDIRS),$(eval $(call subdir_template,install,$(d))))

build : $(foreach d,$(SUBDIRS),build-$(d))

install : $(foreach d,$(SUBDIRS),install-$(d))
	rm -Rf ${PREFIX}/configuration/org.eclipse.osgi
	rm -f ${PREFIX}/configuration/*.log

clean : clean-log $(foreach d,$(SUBDIRS),clean-$(d))
	rm -rf $(PREFIX)/configuration/org.eclipse.osgi

clean-log :
	rm -f $(PREFIX)/alitheia.log $(PREFIX)/hibernate.log
	rm -f $(PREFIX)/derby.log

clean-db :
	rm -rf $(PREFIX)/derbyDB

CL_CONFIG=-Dorg.apache.commons.logging.Log=org.apache.commons.logging.impl.Log4JLogger
LOG4J_CONFIG=-Dlog4j.configuration=file://$(ABS_PREFIX)/configuration/log4j.properties

# $(CONFIG) would typically be used to set system properties.
run :
	cd $(PREFIX) && \
	java $(CONFIG) \
		$(CL_CONFIG) $(LOG4J_CONFIG) \
		-jar org.eclipse.osgi_3.3.0.v20070321.jar -console

run-ui :
	cd ui/webui && $(MAKE) start

stop-ui :
	cd ui/webui && $(MAKE) stop

start : run-ui run




# The default log4j configuration puts the log directly in $(PREFIX) and
# the SQO-OSS logger puts it in the bundle data directory. Handle both.
show-log :
	if test -s $(PREFIX)/logs/alitheia.log  ; then \
		cat $(PREFIX)/logs/alitheia.log ; \
	else \
		cat $(PREFIX)/configuration/org.eclipse.osgi/bundles/[0-9]*/data/logs/alitheia*.log ; \
	fi

DBPATH=alitheia/db/src/main/resources
RUN_DERBY_IJ=java -Dij.protocol=jdbc:derby: -Dij.database=equinox/derbyDB \
		-cp $(DBPATH)/derby-10.3.1.4.jar:$(DBPATH)/tools-10.3.1.4.jar \
		org.apache.derby.tools.ij
show-db :
	$(RUN_DERBY_IJ)

show-db-tables :
	echo "show tables;" | $(RUN_DERBY_IJ) | grep '^APP'

