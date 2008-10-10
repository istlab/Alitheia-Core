TOP_SRCDIR=..
SUBDIRS=clmt skeleton wc contrib quality-model simulation modulemetrics mde multigrep testability

include $(TOP_SRCDIR)/Makefile.metrics

clean-dir :
	for i in $(SUBDIRS); do ( cd $$i && $(MAKE) clean ) ; done
