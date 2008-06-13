TOP_SRCDIR=..

include $(TOP_SRCDIR)/Makefile.bundle

clean-dir :
	rm -rf libs

assembly :
	$(MAVEN) assembly:directory package

install : assembly

