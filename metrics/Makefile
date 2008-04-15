TOP_SRCDIR=..
SUBDIRS=skeleton wc productivity-metric

include $(TOP_SRCDIR)/Makefile.metrics

clean-dir :
	for i in $(SUBDIRS); do ( cd $$i && $(MAKE) clean ) ; done
