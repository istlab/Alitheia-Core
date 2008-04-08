TOP_SRCDIR=../..
SUBDIRS=wslib sclib

include $(TOP_SRCDIR)/Makefile.bundle

clean-dir :
	for i in $(SUBDIRS) ; do ( cd $$i && $(MAKE) clean ) || exit 1 ; done
	rm -rf build target

install : assembly

assembly :
	$(MAVEN) package assembly:assembly install

