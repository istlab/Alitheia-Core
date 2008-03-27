TOP_SRCDIR=../..
include $(TOP_SRCDIR)/Makefile.common

JAVA_RESULTS=CoreHelper.java \
	JobPackage/JobStateHelper.java \
	JobPackage/JobStateHolder.java \
	JobPackage/JobState.java \
	JobPackage


CPP_RESULTS=Alitheia.cpp \
	Alitheia.h

build : ../cpp/src/Alitheia.cpp ../corbaservice/src/eu/sqooss/impl/service/corba/alitheia/CoreHelper.java
	:

# Since the C++ parts are optional, this is allowed to fail. That's
# why there is a true at the end of the command, to force successful
# completion of the command always.
../cpp/src/Alitheia.cpp: Alitheia.idl
	cd ../cpp/src; idl --any --poa --c++-suffix cpp ../../idl/Alitheia.idl; true
	
# Since Java is *not* optional, this must succeed.
../corbaservice/src/eu/sqooss/impl/service/corba/alitheia/CoreHelper.java: Alitheia.idl
	idlj -td ../corbaservice/src -pkgPrefix alitheia eu.sqooss.impl.service.corba -fall Alitheia.idl

install:
#bogus rule, nothing to do here

clean-dir :
	-DIR=../corbaservice/src/eu/sqooss/impl/service/corba/alitheia/; \
	for file in `ls $$DIR/*.java`; \
	do test -f $$file && rm $$file; \
	done
	-DIR=../corbaservice/src/eu/sqooss/impl/service/corba/alitheia/; \
	for file in ${JAVA_RESULTS}; \
	do test -d $$DIR/$$file && rm -r $$DIR/$$file; \
	done
	-DIR=../cpp/src; \
	for file in ${CPP_RESULTS}; \
	do test -f $$DIR/$$file && rm $$DIR/$$file; \
	done
