TOP_SRCDIR=../..
SUBDIRS=wslib sclib

include $(TOP_SRCDIR)/Makefile.bundle

build-dir : build-subdirs

ifeq ($(SUBDIRS),)
build-subdirs :
else
build-subdirs :
	for i in $(SUBDIRS) ; do ( cd $$i && $(MAKE) build-dir ) || exit 1; done
endif

clean-dir :
	for i in $(SUBDIRS) ; do ( cd $$i && $(MAKE) clean ) || exit 1 ; done
	rm -rf build target

install : assembly

assembly :
	$(MAVEN) package assembly:assembly install

