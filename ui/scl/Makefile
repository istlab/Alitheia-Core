TOP_SRCDIR=../..

include $(TOP_SRCDIR)/Makefile.bundle

build-dir :
	# Need to special-case wslib to just generate the jar once
	cd wslib && $(MAKE) build-dir

clean-dir :
	for i in wslib sclib ; do ( cd $$i && $(MAKE) clean ) || exit 1 ; done
	rm -rf build target

install : assembly

assembly :
	$(MAVEN) package assembly:assembly install

