# This Makefile is part of the Alitheia system produced by the SQO-OSS
# consortium and is covered by the same LICENSE as the rest of the system:
# the 2-Clause FreeBSD license which you may find in the LICENSE file
# in the devel/trunk/ directory of the Alitheia development repository.

# This is the top-level Makefile for Alitheia. It builds 
# the Alitheia Core Platform, a collection of metrics and the 
# Corba / C++ bindings for the platform.
#
# The file README-BUILD explains how the build system works.

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


TOP_SRCDIR=.
SUBDIRS=sharedlibs alitheia metrics

include Makefile.common

install-dir :
	( cd ../sqoossrepo && $(MAKE) install TOP_SRCDIR=$(TOPDIR) )

clean-osgi :
	rm -rf $(PREFIX)/configuration/org.eclipse.osgi/

clean-log :
	rm -f $(PREFIX)/alitheia.log $(PREFIX)/hibernate.log $(PREFIX)/derby.log
	rm -f $(PREFIX)/logs/*
	rm -f $(PREFIX)/configuration/*.log

clean-dir : clean-osgi clean-log
	rm -f $(PREFIX)/eu.sqooss*.jar

clean-all : clean
	rm -f $(PREFIX)/*.jar

distclean : clean-all
	-find . -type f -name '*~' | xargs rm
	-find . -type f -name DS_Store | xargs rm

.PHONY : clean-osgi clean-log clean-dir clean-all distclean

show-log :
	@cat $(PREFIX)/logs/alitheia.log

###
# 
# Log4J configuration -- all we need is the right URL for the config file.
#
CL_CONFIG=-Dorg.apache.commons.logging.Log=org.apache.commons.logging.impl.Log4JLogger
LOG4J_PREFIX=$(PREFIX)
ifeq ($(OS),Windows_NT)
LOG4J_PREFIX:=$(subst /cygdrive/c,,$(LOG4J_PREFIX))
endif
LOG4J_CONFIG=-Dlog4j.configuration=file://$(LOG4J_PREFIX)/configuration/log4j.properties

###
#
# Jetty configuration
#
JETTY_CONFIG=-DDEBUG_VERBOSE=1 \
	-DDEBUG_PATTERNS=main,org.mortbay.http \
	-Dorg.mortbay.log.LogFactory.noDiscovery=false


JAVA_CMD=java $(JAVA_CONFIG)
JAVA_DEBUG_ARGS=-Xdebug \
	-Xrunjdwp:transport=dt_socket,server=y,address=8000,suspend=y
JAVA_MONITOR_ARGS=-Dcom.sun.management.jmxremote
JAVA_CORE_ARGS= -DDEBUG $(CL_CONFIG) $(LOG4J_CONFIG) $(JETTY_CONFIG) \
		$(HIBERNATE_CONFIG) \
		-jar org.eclipse.equinox.osgi-3.3.0.jar

start-core : clean-osgi
	cd $(PREFIX) && \
	$(JAVA_CMD) $(JAVA_CORE_ARGS) -console

start-core-bg : clean-osgi
	cd $(PREFIX) && \
	$(JAVA_CMD) $(JAVA_CORE_ARGS) -no-exit

start-core-debug : clean-osgi
	cd $(PREFIX) && \
	$(JAVA_CMD) $(JAVA_DEBUG_ARGS) $(JAVA_CORE_ARGS) -console

start-core-monitor : clean-osgi
	cd $(PREFIX) && \
	$(JAVA_CMD) $(JAVA_MONITOR_ARGS) $(JAVA_CORE_ARGS) -console


.PHONY : start-core start-core-bg start-core-debug start-core-monitor


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

