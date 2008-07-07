TOP_SRCDIR=..

include $(TOP_SRCDIR)/Makefile.bundle

clean-dir :
	rm -rf libs
	rm -rf data/iCopy

assembly :
	$(MAVEN) assembly:directory package
	cd data && $(TAR) xjf iCopy.tar.bz2

install : assembly

