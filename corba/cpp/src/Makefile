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
	ar cqs ../lib/libalitheia.a Alitheia.o logger.o corbahandler.o core.o metric.o

logger: main.o Alitheia.o logger.o corbahandler.o core.o metric.o
	mico-ld -o logger main.o logger.o core.o corbahandler.o Alitheia.o metric.o -lmicocoss2.3.12 -lmicocoss2.3.12 -lboost_thread

clean:
	-rm Alitheia.cpp Alitheia.h
	-rm Alitheia.o
	-rm main.o
	-rm logger.o
	-rm corbahandler.o
	-rm core.o
	-rm metric.o
	-rm ../lib/libalitheia.a
	-rm -r ../lib
