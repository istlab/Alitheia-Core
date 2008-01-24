all: ../lib/libalitheia.a

Alitheia.o: Alitheia.cpp
	mico-c++ -I. -c Alitheia.cpp

logger.o: Alitheia.cpp logger.cpp corbahandler.h logger.h
	mico-c++ -c logger.cpp

metric.o: Alitheia.cpp metric.cpp metric.h
	mico-c++ -c metric.cpp

core.o: Alitheia.cpp core.cpp corbahandler.h core.h
	mico-c++ -c core.cpp

corbahandler.o: Alitheia.cpp corbahandler.cpp corbahandler.h
	mico-c++ -c corbahandler.cpp

../lib/libalitheia.a: Alitheia.o logger.o corbahandler.o core.o metric.o
	test -d ../lib || mkdir ../lib
	test -f ../lib/libalitheia.a && rm ../lib/libalitheia.a
	ar cqs ../lib/libalitheia.a Alitheia.o logger.o corbahandler.o core.o metric.o

clean:
	-rm Alitheia.o
	-rm logger.o
	-rm corbahandler.o
	-rm core.o
	-rm metric.o
	-rm ../lib/libalitheia.a
	-rm -r ../lib
