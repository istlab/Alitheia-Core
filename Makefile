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
# show-log	- Finds the run log and prints it.
#
# After 'make run' you may need to type 'close' on the OSGi console
# to quit the Alitheia system (in production circumstances you would
# not have the console). The run target assumes (and does not check)
# that you have done a 'make install' already.

# This is where OSGi / Equinox is installed under this directory.
PREFIX=equinox

# Subdirectories to build or install from.
SUBDIRS=alitheia

#
# END OF USER CONFIGURATION AREA
#
###

ABS_PREFIX=$(shell cd $(PREFIX) && pwd)

all : build


# Template to carry a target to a subdirectory while preserving the
# PREFIX and Maven attributes.
define subdir_template
$(1)-$(2) :
	cd $(2) && $(MAKE) $(1) PREFIX=$(ABS_PREFIX) WITH_MAVEN=$(WITH_MAVEN)
endef

$(foreach d,$(SUBDIRS),$(eval $(call subdir_template,build,$(d))))
$(foreach d,$(SUBDIRS),$(eval $(call subdir_template,clean,$(d))))
$(foreach d,$(SUBDIRS),$(eval $(call subdir_template,install,$(d))))

build : $(foreach d,$(SUBDIRS),build-$(d))

install : $(foreach d,$(SUBDIRS),install-$(d))
	rm -Rf ${PREFIX}/configuration/org.eclipse.osgi
	rm -f ${PREFIX}/configuration/*.log

clean : $(foreach d,$(SUBDIRS),clean-$(d))
	rm -f $(PREFIX)/alitheia.log
	rm -rf $(PREFIX)/configuration/org.eclipse.osgi

# $(CONFIG) would typically be used to set system properties.
run :
	cd $(PREFIX) && \
	java $(CONFIG) -jar org.eclipse.osgi_3.3.0.v20070321.jar -console

run-ui :
	cd ui/webui && $(MAKE) start

stop-ui :
	cd ui/webui && $(MAKE) stop

start : run-ui run




# The default log4j configuration puts the log directly in $(PREFIX) and
# the SQO-OSS logger puts it in the bundle data directory. Handle both.
show-log :
	if test -f $(PREFIX)/alitheia.log  ; then \
		cat $(PREFIX)/alitheia.log ; \
	else \
		cat $(PREFIX)/configuration/org.eclipse.osgi/bundles/[0-9]*/data/logs/alitheia*.log ; \
	fi

