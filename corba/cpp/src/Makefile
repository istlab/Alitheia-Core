all: ../lib/libalitheia.a

Alitheia.o: Alitheia.h
	mico-c++ -g -I. -c Alitheia.cpp

logger.o: Alitheia.h logger.cpp corbahandler.h logger.h
	mico-c++ -g -c logger.cpp

metric.o: Alitheia.h metric.cpp metric.h
	mico-c++ -g -c metric.cpp

dbobject.o: Alitheia.h dbobject.cpp dbobject.h fds.h
	mico-c++ -g -c dbobject.cpp

core.o: Alitheia.h core.cpp corbahandler.h core.h metric.h job.h
	mico-c++ -g -c core.cpp

job.o: Alitheia.h job.cpp job.h corbahandler.h
	mico-c++ -g -c job.cpp

fds.o: Alitheia.h fds.cpp fds.h corbahandler.h
	mico-c++ -g -c fds.cpp

database.o: Alitheia.h dbobject.h database.cpp database.h corbahandler.h
	mico-c++ -g -c database.cpp

corbahandler.o: Alitheia.h corbahandler.cpp corbahandler.h
	mico-c++ -g -c corbahandler.cpp

Alitheia.h: ../../idl/Alitheia.idl
	idl --any --poa --c++-suffix cpp ../../idl/Alitheia.idl

../lib/libalitheia.a: Alitheia.o logger.o corbahandler.o core.o metric.o job.o dbobject.o database.o fds.o
	test -d ../lib || mkdir ../lib
	test ! -f ../lib/libalitheia.a || rm ../lib/libalitheia.a
	ar cqs ../lib/libalitheia.a Alitheia.o logger.o corbahandler.o core.o metric.o job.o dbobject.o database.o fds.o

clean:
	test ! -f Alitheia.o || rm Alitheia.o
	test ! -f Alitheia.cpp || rm Alitheia.cpp
	test ! -f Alitheia.h || rm Alitheia.h
	test ! -f logger.o || rm logger.o
	test ! -f corbahandler.o || rm corbahandler.o
	test ! -f core.o || rm core.o
	test ! -f job.o || rm job.o
	test ! -f fds.o || rm fds.o
	test ! -f metric.o || rm metric.o
	test ! -f dbobject.o || rm dbobject.o
	test ! -f ../lib/libalitheia.a || rm ../lib/libalitheia.a
	test ! "`echo ../lib/*`" == '../lib/*' || ( test -d ../lib && rm -r ../lib ) || true
