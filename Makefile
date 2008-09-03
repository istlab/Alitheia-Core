# This Makefile is part of the Alitheia system produced by the SQO-OSS
# consortium and is covered by the same LICENSE as the rest of the system:
# the 2-Clause FreeBSD license which you may find in the LICENSE file
# in the devel/trunk/ directory of the Alitheia development repository.

# This is the top-level Makefile for Alitheia. It builds 
# the Alitheia Core Platform, a collection of metrics and the 
# Corba / C++ bindings for the platform.
#
# The file README-BUILD.txt explains how the build system works.
# The comments in this Makefile explain individual targets.
#
# Most useful targets are:
#
# clean           - Clean up the entire system for a recompile
# build (default) - Compile the system
# install         - Install the system into the equinox/ directory
# start-core      - Run the Core Platform
# stop-core       - Stop the Core Platform, regardless of how it was started
#                   (This requires curl).
#
# TODO: self-contained means to stop-core
# 
# Most of the time, those four targets (in that order) are what you
# need to (re)build the system from scratch. For details and 
# configuration information, see README-BUILD and the comments
# in this Makefile and in Makefile.common.


###
#
# Source configuration
#

# The top of the source tree. That's here.
TOP_SRCDIR=.
# Subdirectories that need special building.
SUBDIRS=sqoossrepo alitheia metrics ui specs

###
#
# Build targets
#

# Get all of the common targets like 'all' 'build' and 'install'.
include Makefile.common

build-extlibs :
	cd sqoossrepo && $(MAKE) && $(MAKE) install

build-core : build-extlibs
	cd alitheia && $(MAKE) && $(MAKE) install
	
build-metrics : 
	cd metrics && $(MAKE) && $(MAKE) install

# The standard clean target calls clean-dir to clean up things *here*,
# so we remove the installed bundles from the prefix. That's like
# uninstall, so we might want to reconsider that long-term.
#
# TODO: decide on a install / uninstall scheme.
clean-dir : clean-osgi clean-log
	rm -f $(PREFIX)/eu.sqooss*.jar

# OSGi futzes around with installed bundles, so this is an extra
# clean target to remove the copies of bundles that it creates.
# Used as part of 'start-core' to make sure that we use the
# newest bundles.
clean-osgi :
	rm -rf $(PREFIX)/configuration/org.eclipse.osgi/
	rm -f $(PREFIX)/configuration/*.log
	rm -rf $(PREFIX)/configuration-specs/org.eclipse.osgi/
	rm -f $(PREFIX)/configuration-specs/*.log

# Throw away all the logs that the system creates.
clean-log :
	rm -f $(PREFIX)/alitheia.log $(PREFIX)/hibernate.log $(PREFIX)/derby.log
	rm -f $(PREFIX)/logs/*
	rm -f $(PREFIX)/configuration/*.log

# Clean everything *and* also throw away the installed third-party
# bundles, so we get everything anew.
clean-all : clean clean-osgi clean-log
	rm -f $(PREFIX)/*.jar
	cd sqoossrepo && $(MAKE) clean-all
	cd doc && $(MAKE) clean

# Clean everything and remove all kinds of cruft files that might
# have shown up in the source tree.
distclean : clean-all
	-find . -type f -name '*~' | xargs rm
	-find . -type f -name DS_Store | xargs rm

.PHONY : clean-osgi clean-log clean-dir clean-all distclean

###
#
# Run targets are in a separate file so they can be more
# conveniently distributed with the binaries.

include Makefile.run

###
#
# Testing targets
#

specs : clean-osgi
	cd specs && $(MAKE) prepare-data
	cd $(PREFIX) && \
	$(JAVA_CMD) \
	-Dosgi.configuration.area=$(PREFIX)/configuration-specs \
	$(JAVA_CORE_ARGS) -no-exit

specs-debug : clean-osgi
	cd specs && $(MAKE) prepare-data
	cd $(PREFIX) && \
	$(JAVA_CMD) \
	$(JAVA_DEBUG_ARGS) \
	-Dosgi.configuration.area=$(PREFIX)/configuration-specs \
	$(JAVA_CORE_ARGS) -no-exit -console

.PHONY : specs specs-debug


###
#
# Documentation targets
#
#

.PHONY : doc javadoc manual

doc : javadoc manual

manual :  
	cd doc && XSLTS=${XSLT} $(MAKE)

javadoc: ## Write maven based javadoc rules	
	mvn -Dproject.reporting.outputDirectory=doc javadoc:javadoc
###
#
# Transitional targets
#
# 
define deprecated
$(1) :
	@echo "###" ; echo "### $(1) : use $(2) instead." ; echo "###"
	@sleep 1
	$(MAKE) $(2)

.PHONY : $(1)
endef

$(eval $(call deprecated,run,start-core))
$(eval $(call deprecated,run-bg,start-core-bg))
$(eval $(call deprecated,debug,start-core-debug))
$(eval $(call deprecated,monitor,start-core-monitor))

