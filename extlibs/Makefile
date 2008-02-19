##Makefile to autobuild the bundled libraries

TOP_SRCDIR?=..

BUILDDIR=build
DIRS=$(shell find . -maxdepth 1 -mindepth 1 -not -regex ".*build" -not -regex ".*svn" -type d)

ECLIPSEFILES=.project .settings .classpath build.properties
EXCLUDEFILES=.svn  META-INF 
EXCLUDEFILES+=${ECLIPSEFILES}


JARS=$(addsuffix .jar , $(DIRS))
LOCAL_JARS=$(wildcard *.jar)

all: build

define jar_bundler
$(BUILDDIR)/$(1).jar: $(1)
	@echo Creating bundle $(1) 
	@excl=`echo "(${EXCLUDEFILES})" | sed -e s/\ \ */\|/g` && \
	incl=`cd $(1) && find . -type f |egrep -v "^$$$$excl" | tr '\n' ' ' |sed -e "s, $$$$,," -e "s,\ , -C $(1) ,g" -e "s,^, -C $(1) ,"` && \
	jar -cvmf $(1)/META-INF/MANIFEST.MF ${BUILDDIR}/$(1).jar $$$$incl >/dev/null 
endef

$(foreach d,$(DIRS),$(eval $(call jar_bundler,$(strip $(d)))))

builddir: 
	@test -d ${BUILDDIR} || mkdir ${BUILDDIR}

build: builddir $(addprefix $(BUILDDIR)/,$(JARS))

install: build
	-cp -v ${BUILDDIR}/*.jar ${TOP_SRCDIR}/equinox
	-cp -v $(LOCAL_JARS) ${TOP_SRCDIR}/equinox

clean:
	rm -Rf ${BUILDDIR}
