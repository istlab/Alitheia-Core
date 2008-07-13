TOP_SRCDIR=..
SUBDIRS=clmt skeleton wc productivity-metric quality-model simulation modulemetrics

include $(TOP_SRCDIR)/Makefile.metrics

clean-dir :
	for i in $(SUBDIRS); do ( cd $$i && $(MAKE) clean ) ; done
