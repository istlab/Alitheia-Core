JAVA_RESULTS=CoreHelper.java \
	CoreHolder.java \
	Core.java \
	CoreOperations.java \
	CorePOA.java \
	_CoreStub.java \
	LoggerHelper.java \
	LoggerHolder.java \
	Logger.java \
	LoggerOperations.java \
	LoggerPOA.java \
	_LoggerStub.java \
	MetricHelper.java \
	MetricHolder.java \
	Metric.java \
	MetricOperations.java \
	MetricPOA.java \
	_MetricStub.java \
	JobHelper.java \
	JobHolder.java \
	Job.java \
	JobOperations.java \
	JobPOA.java \
	_JobStub.java \
	JobPackage/JobStateHelper.java \
	JobPackage/JobStateHolder.java \
	JobPackage/JobState.java \
	JobPackage

CPP_RESULTS=Alitheia.cpp \
	Alitheia.h

all: ../cpp/src/Alitheia.cpp ../corbaservice/src/eu/sqooss/impl/service/corba/alitheia/CoreHelper.java

build : all

-include ../../Makefile.common

../cpp/src/Alitheia.cpp: Alitheia.idl
	cd ../cpp/src; idl --poa --c++-suffix cpp ../../idl/Alitheia.idl; cd ../../idl
	
../corbaservice/src/eu/sqooss/impl/service/corba/alitheia/CoreHelper.java: Alitheia.idl
	idlj -td ../corbaservice/src -pkgPrefix alitheia eu.sqooss.impl.service.corba -fall Alitheia.idl

install:
#bogus rule, nothing to do here

clean:
	-DIR=../corbaservice/src/eu/sqooss/impl/service/corba/alitheia/; \
	for file in ${JAVA_RESULTS}; \
	do test -f $$DIR/$$file && rm $$DIR/$$file; \
	done
	-DIR=../corbaservice/src/eu/sqooss/impl/service/corba/alitheia/; \
	for file in ${JAVA_RESULTS}; \
	do test -d $$DIR/$$file && rm -r $$DIR/$$file; \
	done
	-DIR=../cpp/src; \
	for file in ${CPP_RESULTS}; \
	do test -f $$DIR/$$file && rm $$DIR/$$file; \
	done
