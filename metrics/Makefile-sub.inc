# This is a GNU Makefile

# This Makefile is part of the Alitheia system produced by the SQO-OSS
# consortium and is covered by the same LICENSE as the rest of the system:
# the 2-Clause FreeBSD license which you may find in the LICENSE file.

# This Makefile builds OSGi bundles in a straightforward fashion. It
# has none of the sophistication of the Maven setup in that it doesn't
# check for updates, downloads nothing, ignores other installed files,
# and doesn't handle multiple concurrent versions of the build. Some people
# consider this a feature. To use it, *include* it from a subdirectory.
# Before including, you should set two variables:
#
# 	BUNDLE_NAME	The name of the bundle itself, probably derived
#			from the directory name or the classes in the bundle.
#	BUNDLE_VERSION	The version number of the bundle; this should match
#			what is in the manifest and is appended to the
#			name of the jar file.
#
# Targets build, install and clean are defined.

# Extra dependencies for this bundle. These should point to the sources
# of other bundles. Everyone depends on the logger, so add it always.
EXTRA_CP:=$(EXTRA_CP):../../alitheia/core/src

# Optionally, the manifest name may be different from the bundle name.
# This is a historical mistake, but not easy to correct.
BUNDLE_MANIFEST_NAME?=$(BUNDLE_NAME)

#
# END OF USER CONFIGURATION
#
###

TOP_SRCDIR?=$(shell cd ../.. && pwd)
PREFIX?=$(shell cd ../../equinox && pwd)
CP:=$(shell /bin/sh $(TOP_SRCDIR)/tools/setcp.sh ../..)
EXTRA_CP:=${CP}:$(EXTRA_CP)

# Find the source files, the dependency jars and other resources
SRC=$(shell cd src && find eu -name *.java)
JARS=$(shell test -d src/main/resources && find src/main/resources -name '*.jar')
RESOURCES=$(shell test -d src/main/resources && cd src/main/resources && ls -1)
HBM_RESOURCES=$(shell cd src/ && find eu -name '*.hbm.xml')

# This is (hardcoded) the OSGi framework classpath.
EQUINOX_CP=$(PREFIX)/org.eclipse.osgi_3.3.0.v20070321.jar:$(PREFIX)/org.eclipse.osgi.util_3.1.100.v20060918.jar:$(PREFIX)/org.eclipse.osgi.services_3.1.100.v20060918.jar:

# All metrics need some specific jars as well for support.
METRICS_CP=$(PREFIX)/org.apache.commons.codec_1.3.0.jar:

# Add the dependency jars to the classpath.
$(foreach d,$(JARS),$(eval EXTRA_CP:=$(EXTRA_CP)$(d):))
EXTRA_CP:=$(EXTRA_CP):$(EQUINOX_CP):$(METRICS_CP):src
ifeq ($(OS),Windows_NT)
EXTRA_CP:=$(subst /,\,$(EXTRA_CP))
EXTRA_CP:=$(subst :,;,$(EXTRA_CP))
endif
CLASSPATH=$(EXTRA_CP)

# Basic targets all, build, install, clean
all: build-all build-jar

install: build-jar
	cp target/$(BUNDLE_NAME)-$(BUNDLE_VERSION).jar $(PREFIX)/eu.sqooss.metrics.$(BUNDLE_NAME)_$(BUNDLE_VERSION).jar

clean:
	rm -rf target/ build/ bin/
	rm -f $(BUNDLE_NAME)-*.jar target/$(BUNDLE_NAME)-*.jar
	rm -f $(PREFIX)/$(BUNDLE_NAME)-$(BUNDLE_VERSION).jar


# Depend on s/java/class/ files.
build-all: build build/.prebuild

build-jar: target target/$(BUNDLE_NAME)-$(BUNDLE_VERSION).jar

target:
	mkdir target
	test -d target

build:
	mkdir build
	test -d build

CHECKSTYLE_DIR=$(TOP_SRCDIR)/../../tools/javatools/
checkstyle:
	cd src && \
	java -jar $(CHECKSTYLE_DIR)/checkstyle-all-4.3.jar \
		-c $(CHECKSTYLE_DIR)/sun_checks.xml \
		$(SRC)


define resource_template
build/$(1) : src/main/resources/$(1)
	if test -d src/main/resources/$(1) ; then \
		cp -R src/main/resources/$(1) build/ ; \
		rm -rf `find build/ -name .svn` ; \
	else \
		cp src/main/resources/$(1) build/ ; \
	fi
endef

define hbm_resource_template
build/$(1) : src/$(1)
	@-mkdir -p build/$(dir $(1))
	cp src/$(1) build/$(dir $(1))
endef

ifeq ($(WITH_MAVEN),)
# Depend on the class files and all the copied-over resource files
target/$(BUNDLE_NAME)-$(BUNDLE_VERSION).jar : manifest/$(BUNDLE_MANIFEST_NAME).mf build build/.prebuild $(RESOURCES:%=build/%) $(HBM_RESOURCES:%=build/%)
	cd build/ && jar cfm ../target/$(BUNDLE_NAME)-$(BUNDLE_VERSION).jar ../manifest/$(BUNDLE_MANIFEST_NAME).mf *
endif

$(foreach d,$(RESOURCES),$(eval $(call resource_template,$(d))))
$(foreach d,$(HBM_RESOURCES),$(eval $(call hbm_resource_template,$(d))))

build/.prebuild : $(SRC:%=src/%)
	javac -g -Xlint:-path -cp "$(CLASSPATH)" -source 1.5 -d build/ $?
	touch build/.prebuild

JAVADEP=perl ../../../../tools/java_dependency_generator.pl
depend :
	for i in `find . -name *.java`; do echo $$i ; grep "^import *eu.sqooss" $$i; done | $(JAVADEP) $(BUNDLE_NAME) > Makefile.dep

-include Makefile.dep

