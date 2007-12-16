##Makefile to autobuild the bundled libraries

BUILDDIR=build
DIRS=$(shell find . -type d -depth 1 -not -regex ".*build" -not -regex ".*svn")
JARS=$(addsuffix .jar , $(DIRS))

all: build

%.jar: %
	jar -cvmf $</META-INF/MANIFEST.MF ${BUILDDIR}/$<.jar $<  >/dev/null 2>&1

builddir: 
	-mkdir ${BUILDDIR}

build: builddir $(JARS)

install:
#	cp ${BUILDDIR}/*.jar ${TOP_SRCDIR}/equinox

clean:
	rm -Rf ${BUILDDIR}
