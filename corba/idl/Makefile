TOP_SRCDIR=../..
include $(TOP_SRCDIR)/Makefile.config

all : build

build :
	$(MAVEN)

clean : clean-dir
	rm -rf build target
	rm -rf ../corbaservice/src/eu/sqooss/impl/service/corba/alitheia/*.java
	rm -rf ../corbaservice/src/eu/sqooss/impl/service/corba/alitheia/JobPackage

install:

help:
	@echo $(MAVEN)

.PHONY : all build clean clean-dir install help
