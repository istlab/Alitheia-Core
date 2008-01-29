all: ../lib/libalitheia.a

Alitheia.o: Alitheia.cpp Alitheia.h
	mico-c++ -I. -c Alitheia.cpp

logger.o: Alitheia.h logger.cpp corbahandler.h logger.h
	mico-c++ -c logger.cpp

metric.o: Alitheia.h metric.cpp metric.h
	mico-c++ -c metric.cpp

core.o: Alitheia.h core.cpp corbahandler.h core.h metric.h job.h
	mico-c++ -c core.cpp

job.o: Alitheia.h job.cpp job.h corbahandler.h
	mico-c++ -c job.cpp

corbahandler.o: Alitheia.cpp corbahandler.cpp corbahandler.h
	mico-c++ -c corbahandler.cpp

../lib/libalitheia.a: Alitheia.o logger.o corbahandler.o core.o metric.o job.o
	test -d ../lib || mkdir ../lib
	test ! -f ../lib/libalitheia.a || rm ../lib/libalitheia.a
	ar cqs ../lib/libalitheia.a Alitheia.o logger.o corbahandler.o core.o metric.o job.o

clean:
	test ! -f Alitheia.o || rm Alitheia.o
	test ! -f logger.o || rm logger.o
	test ! -f corbahandler.o || rm corbahandler.o
	test ! -f core.o || rm core.o
	test ! -f job.o || rm job.o
	test ! -f metric.o || rm metric.o
	test ! -f ../lib/libalitheia.a || rm ../lib/libalitheia.a
	test ! "`echo ../lib/*`" == '../lib/*' || ( test -d ../lib && rm -r ../lib ) || true
