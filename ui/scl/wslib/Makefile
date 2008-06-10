TOP_SRCDIR=../../..
WITNESS=target/src-generation-completed

include $(TOP_SRCDIR)/Makefile.bundle

clean-dir : clean-deps clean-src

build-dir : $(WITNESS)

$(WITNESS) : ws.wsdl
	$(MAKE) install-deps compile-wsdl
	test -f $(WITNESS)

###
#
# This library is unusual in that most of the source
# code, perhaps all of it, is auto-generated from the
# WSDL produced by the running Alitheia Core. These
# extra targets here help in updating the web-services
# client library when the available services change.
#
# The usual target is
#	update-wsdl
# which does everything. Hopefully it does everything right.

update-wsdl : clean-wsdl clean-deps \
	fetch-wsdl \
	install-deps \
	compile-wsdl

# Retrieve the WSDL from the running core platform.
fetch-wsdl : clean-wsdl ws.wsdl

# Cleans up the source directory.
clean-src :
	rm -rf src $(WITNESS)

# Cleans up the WSDL file which was fetched.
clean-wsdl :
	rm -f ws.wsdl $(WITNESS)

# Cleans up all the dependency jars needed for compilation.
clean-deps :
	rm -rf target/dependency

# Use Maven to install all the dependency jars needed for
# compilation into target/dependency; this is used to get
# the right versions of everything out of the sqooss repo.
install-deps :
	$(MAVEN) dependency:copy-dependencies

# Retrieve the actual WSDL sources. This requires a running
# Alitheia Core system on the local machine at the default port.
# For developers, this is straightforward; updated sources should
# be committed to the source tree.
ws.wsdl :
	$(FETCH) "http://localhost:8088/sqooss/services/ws?wsdl" ws.wsdl

# This is one of the rare times we need to invoke java from a Makefile,
# and it is ugly. We have a whole bunch of jars to work with:
# dependency jars (listed in pom.xml and installed by Maven into
# target/dependency) and the jars of the WSDL compilation application
# itself. We need to construct a classpath for this; we also need
# to handle varying (Windows vs. UNIX) classpath formatting conventions.

# Here's the location of the two compilation application jars.
CODEGEN_PATH=$(TOP_SRCDIR)/tools
CODEGEN_LIBS=$(CODEGEN_PATH)/axis2-adb-codegen-1.1.jar \
	$(CODEGEN_PATH)/axis2-codegen-1.1.jar

# These are the ones installed by Maven.
EXT_LIBS=$(wildcard target/dependency/*.jar)

# First we concatenate the whole thing into a colon-separated list.
_ALL_CP=$(shell echo $(EXT_LIBS) $(CODEGEN_LIBS) | tr ' ' ':')
# Now adjust for Windows : -> ; and / -> \ in classpath.
ifeq ($(OS),Windows_NT)
ALL_CP=$(subst :,;,$(subst /,\,$(_ALL_CP)))
else
ALL_CP=$(_ALL_CP)
endif

# The invocation of WSDL2Java. For more options run the tool from the console.
compile-wsdl : clean-src
	$(JAVA_CMD) -cp '$(ALL_CP)' org.apache.axis2.wsdl.WSDL2Java \
		-uri ws.wsdl -s -u -o . \
		-p eu.sqooss.ws.client \
		-ns2p http://datatypes.services.web.service.impl.sqooss.eu/xsd=eu.sqooss.ws.client.datatypes,http://services.web.service.sqooss.eu/xsd=eu.sqooss.ws.client.ws
	rm -f build.xml
	touch $(WITNESS)



.PHONY : update-wsdl clean-wsdl clean-deps fetch-wsdl install-deps compile-wsdl install-wsdl
