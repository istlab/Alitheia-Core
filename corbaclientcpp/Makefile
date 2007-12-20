all: logger

Alitheia.cpp: Alitheia.idl
	idl --poa --c++-suffix cpp Alitheia.idl

Alitheia.o: Alitheia.cpp
	mico-c++ -I. -c Alitheia.cpp

main.o: Alitheia.cpp main.cpp logger.h
	mico-c++ -c main.cpp

logger.o: Alitheia.cpp logger.cpp corbahandler.h logger.h
	mico-c++ -c logger.cpp

core.o: Alitheia.cpp core.cpp corbahandler.h core.h
	mico-c++ -c core.cpp

corbahandler.o: Alitheia.cpp corbahandler.cpp corbahandler.h
	mico-c++ -c corbahandler.cpp

logger: main.o Alitheia.o logger.o corbahandler.o core.o
	mico-ld -o logger main.o logger.o core.o corbahandler.o Alitheia.o -lmicocoss2.3.12 -lmicocoss2.3.12 -lboost_thread

clean:
	-rm Alitheia.cpp Alitheia.h
	-rm Alitheia.o
	-rm main.o
	-rm logger.o
	-rm corbahandler.o
	-rm core.o
	-rm logger
