TOP_SRCDIR=../..

include $(TOP_SRCDIR)/Makefile.bundle

ass : assemble

assemble : build deps assembly

assembly :
	$(MAVEN) assembly:assembly

deps :
	$(MAVEN) dependency:copy-dependencies


