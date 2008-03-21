# This Makefile is part of the Alitheia system produced by the SQO-OSS
# consortium and is covered by the same LICENSE as the rest of the system:
# the 2-Clause FreeBSD license which you may find in the LICENSE file
# in the devel/trunk/ directory of the Alitheia development repository.

# This is the top-level Makefile for Alitheia. It builds 
# the Alitheia Core Platform, a collection of metrics and the 
# Corba / C++ bindings for the platform.
#
# The file README-BUILD explains how the build system works.
# The comments in this Makefile explain individual targets.
#
# Most useful targets are:
#
# clean           - Clean up the entire system for a recompile
# build (default) - Compile the system
# install         - Install the system into the equinox/ directory
# start-core      - Run the Core Platform
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
# Note that ui/ is not included here.
# 
# TODO: Add ui/ to the build.
SUBDIRS=sqoossrepo sharedlibs alitheia metrics

###
#
# Build targets
#

# Get all of the common targets like 'all' 'build' and 'install'.
include Makefile.common

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

# Clean everything and remove all kinds of cruft files that might
# have shown up in the source tree.
distclean : clean-all
	-find . -type f -name '*~' | xargs rm
	-find . -type f -name DS_Store | xargs rm

.PHONY : clean-osgi clean-log clean-dir clean-all distclean

###
# 
# Run targets.
#
# Launching the Alitheia Core Platform is more than a matter of executing
# a single command. There are a bunch of Java options that are needed.
# These targets provide various ways of starting the system.
#
# start-core         - run with console
# start-core-bg      - run in background
# start-core-debug   - run with console and debug mode
# start-core-monitor - run with console and remote monitoring
#

# Log4J configuration -- all we need is the right URL for the config file.
CL_CONFIG=-Dorg.apache.commons.logging.Log=org.apache.commons.logging.impl.Log4JLogger
LOG4J_PREFIX=$(PREFIX)
ifeq ($(OS),Windows_NT)
LOG4J_PREFIX:=$(subst /cygdrive/c,,$(LOG4J_PREFIX))
endif
LOG4J_CONFIG=-Dlog4j.configuration=file://$(LOG4J_PREFIX)/configuration/log4j.properties

# Jetty configuration.
JETTY_CONFIG=-DDEBUG_VERBOSE=1 \
	-DDEBUG_PATTERNS=main,org.mortbay.http \
	-Dorg.mortbay.log.LogFactory.noDiscovery=false


# Additional arguments for Java. We have special arguments for
# debug and monitor mode, as well as the regular configuraiton
# settings to get OSGi up.
JAVA_DEBUG_ARGS=-Xdebug \
	-Xrunjdwp:transport=dt_socket,server=y,address=8000,suspend=y
JAVA_MONITOR_ARGS=-Dcom.sun.management.jmxremote
JAVA_CORE_ARGS= -DDEBUG $(CL_CONFIG) $(LOG4J_CONFIG) $(JETTY_CONFIG) \
		$(HIBERNATE_CONFIG) \
		-jar org.eclipse.equinox.osgi-3.3.0.jar

# Start the core system with a console. Use 'close' in the console to quit.
start-core : clean-osgi
	cd $(PREFIX) && \
	$(JAVA_CMD) $(JAVA_CORE_ARGS) -console

# Start the core without a console. Use 'make stop-core' to stop it.
start-core-bg : clean-osgi
	cd $(PREFIX) && \
	$(JAVA_CMD) $(JAVA_CORE_ARGS) -no-exit

# Start the core in debug mode with console.
start-core-debug : clean-osgi
	cd $(PREFIX) && \
	$(JAVA_CMD) $(JAVA_DEBUG_ARGS) $(JAVA_CORE_ARGS) -console

# Start the core with remote monitoring and console.
start-core-monitor : clean-osgi
	cd $(PREFIX) && \
	$(JAVA_CMD) $(JAVA_MONITOR_ARGS) $(JAVA_CORE_ARGS) -console

# Stop the core system. This works only if the system is running
# on the default port (8088) as configured in config.ini.
stop-core :

# Display the system log (if it is in its default location).
show-log :
	@cat $(PREFIX)/logs/alitheia.log

.PHONY : start-core start-core-bg start-core-debug start-core-monitor 
.PHONY : stop-core show-log


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

