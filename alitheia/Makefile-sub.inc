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
EXTRA_CP:=$(EXTRA_CP)../logger/src/:

# Optionally, the manifest name may be different from the bundle name.
# This is a historical mistake, but not easy to correct.
BUNDLE_MANIFEST_NAME?=$(BUNDLE_NAME)

#
# END OF USER CONFIGURATION
#
###


# Find the source files, the dependency jars and other resources
SRC=$(shell cd src && find eu -name *.java)
JARS=$(shell test -d src/main/resources && find src/main/resources -name '*.jar')
RESOURCES=$(shell test -d src/main/resources && cd src/main/resources && find . -type f)

# Add the dependency jars to the classpath.
$(foreach d,$(JARS),$(eval EXTRA_CP:=$(EXTRA_CP)$(d):))

# This is (hardcoded) the OSGi framework classpath.
EQUINOX_CP=$(PREFIX)/org.eclipse.osgi_3.3.0.v20070321.jar:$(PREFIX)/org.eclipse.osgi.util_3.1.100.v20060918.jar:$(PREFIX)/org.eclipse.osgi.services_3.1.100.v20060918.jar

# Basic targets all, build, install, clean
all: check-builddir build-all build-jar

build: all

install: build-jar
	cp target/$(BUNDLE_NAME)-$(BUNDLE_VERSION).jar $(PREFIX)

clean:
	rm -rf build/*
	rm -f $(BUNDLE_NAME)-*.jar target/$(BUNDLE_NAME)-*.jar

# We create a separate directory to hold the build artifacts.
check-builddir:
	@test -d build || mkdir build ; test -d build

# Depend on s/java/class/ files.
build-all: $(SRC:%.java=build/%.class)

build-jar: target target/$(BUNDLE_NAME)-$(BUNDLE_VERSION).jar

target:
	mkdir target

ifeq ($(WITH_MAVEN),)
# Depend on the class files and all the copied-over resource files
target/$(BUNDLE_NAME)-$(BUNDLE_VERSION).jar : $(SRC:%.java=build/%.class) $(RESOURCES:%=build/%)
	cd build/ && jar cfm ../target/$(BUNDLE_NAME)-$(BUNDLE_VERSION).jar ../manifest/$(BUNDLE_MANIFEST_NAME).mf .

$(RESOURCES:%=build/%) : $(RESOURCES:%=src/main/resources/%)
	cd src/main/resources && cp -v * ../../../build

build/%.class : src/%.java
	javac -Xlint -source 1.5 -cp $(EXTRA_CP)$(EQUINOX_CP):src/ -d build/ $<
endif

