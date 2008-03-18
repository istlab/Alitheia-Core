##Makefile to autobuild the bundled libraries

TOP_SRCDIR?=..

BUILDDIR=build
DIRS=$(shell for i in * ; do test -d "$$i" && echo "$$i" ; done | grep -v build)

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
	-cp ${BUILDDIR}/*.jar ${TOP_SRCDIR}/equinox
	-cp $(LOCAL_JARS) ${TOP_SRCDIR}/equinox

clean:
	rm -Rf ${BUILDDIR}
