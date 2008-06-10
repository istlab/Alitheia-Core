TOP_SRCDIR=..
SUBDIRS=clmt skeleton wc productivity-metric quality-model simulation

include $(TOP_SRCDIR)/Makefile.metrics

clean-dir :
	for i in $(SUBDIRS); do ( cd $$i && $(MAKE) clean ) ; done
