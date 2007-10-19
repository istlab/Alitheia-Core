BUNDLE_MANIFEST_NAME?=$(BUNDLE_NAME)

SRC=$(shell cd src && find eu -name *.java)

EQUINOX_CP=../../equinox/org.eclipse.osgi_3.3.0.v20070321.jar:../../equinox/org.eclipse.osgi.util_3.1.100.v20060918.jar:../../equinox/org.eclipse.osgi.services_3.1.100.v20060918.jar
EXTRA_CP:=$(EXTRA_CP)../logger/src/:

JARS=$(shell test -d src/main/resources && find src/main/resources -name '*.jar')

$(foreach d,$(JARS),$(eval EXTRA_CP:=$(EXTRA_CP)$(d):))

all: check-builddir build-all build-jar

install: all
	cp $(BUNDLE_NAME)-$(BUNDLE_VERSION).jar ../../equinox

clean:
	rm -rf build/*
	rm -f $(BUNDLE_NAME)-*.jar

check-builddir:
	@test -d build || mkdir build ; test -d build

build-all: $(SRC:%.java=build/%.class)

build-jar: build-resources
	cd build/ && jar cfm ../$(BUNDLE_NAME)-$(BUNDLE_VERSION).jar ../manifest/$(BUNDLE_MANIFEST_NAME).mf .

build-resources:
	@-cd src/main/resources && cp -vR . ../../../build

build/%.class : src/%.java
	javac -Xlint -source 1.5 -cp $(EXTRA_CP)$(EQUINOX_CP):src/ -d build/ $<
