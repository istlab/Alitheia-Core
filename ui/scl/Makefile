TOP_SRCDIR=../..
SUBDIRS=wslib sclib

include $(TOP_SRCDIR)/Makefile.bundle

clean-dir :
	for i in $(SUBDIRS) ; do ( cd $$i && $(MAKE) clean ) || exit 1 ; done
