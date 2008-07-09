TOP_SRCDIR=..

include $(TOP_SRCDIR)/Makefile.bundle

clean-dir :
	rm -rf libs
	rm -rf data/iCopy
	rm -rf data/savonet-mail

assembly : prepare-data
	$(MAVEN) assembly:directory package

prepare-data:
	cd data && rm -rf iCopy && $(TAR) jxf iCopy.tar.bz2
	cd data && rm -rf savonet-mail && $(TAR) jxf savonet-mail.tar.bz2

install : assembly

