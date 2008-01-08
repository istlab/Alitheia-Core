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
	_MetricStub.java

CPP_RESULTS=Alitheia.cpp \
	Alitheia.h

all: Alitheia.idl
	idlj -td ../corbaservice/src -pkgPrefix alitheia eu.sqooss.impl.service -fall Alitheia.idl
	cd ../cpp/src; idl --poa --c++-suffix cpp ../../idl/Alitheia.idl; cd ../../idl

install:
#bogus rule, nothing to do here

clean:
	-DIR=../corbaservice/src/eu/sqooss/impl/service/alitheia/; \
	for file in ${JAVA_RESULTS}; \
	do test -f $$DIR/$$file && rm $$DIR/$$file; \
	done
	-DIR=../cpp/src; \
	for file in ${CPP_RESULTS}; \
	do test -f $$DIR/$$file && rm $$DIR/$$file; \
	done
