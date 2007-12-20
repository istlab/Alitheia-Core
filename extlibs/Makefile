##Makefile to autobuild the bundled libraries

BUILDDIR=build
DIRS=$(shell find . -maxdepth 1 -mindepth 1 -not -regex ".*build" -not -regex ".*svn" -type d)

ECLIPSEFILES=.project .settings .classpath build.properties
EXCLUDEFILES=.svn  META-INF 
EXCLUDEFILES+=${ECLIPSEFILES}


JARS=$(addsuffix .jar , $(DIRS))

all: build

%.jar: %
	@echo Creating bundle $<.jar && \
	excl=`echo ${EXCLUDEFILES} | sed -e s/\ /\|/g` && \
	incl=`cd $< && find . -type f |egrep -v $$excl | tr '\n' ' ' |sed -e "s, $$,," -e "s,\ , -C $< ,g" -e "s,^, -C $< ,"` && \
	jar -cvmf $</META-INF/MANIFEST.MF ${BUILDDIR}/$<.jar $$incl >/dev/null 


builddir: 
	-mkdir ${BUILDDIR}

build: builddir $(JARS)

install:
#cp -v ${BUILDDIR}/*.jar ${TOP_SRCDIR}/equinox

clean:
	rm -Rf ${BUILDDIR}
