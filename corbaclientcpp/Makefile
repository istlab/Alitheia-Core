all: logger

Alitheia.cpp: Alitheia.idl
	idl --poa --c++-suffix cpp Alitheia.idl

Alitheia.o: Alitheia.cpp
	mico-c++ -I. -c Alitheia.cpp

main.o: Alitheia.cpp main.cpp
	mico-c++ -c main.cpp

logger.o: Alitheia.cpp logger.cpp
	mico-c++ -c logger.cpp

corbahandler.o: Alitheia.cpp corbahandler.cpp
	mico-c++ -c corbahandler.cpp

logger: main.o Alitheia.o logger.o corbahandler.o
	mico-ld -o logger main.o logger.o corbahandler.o Alitheia.o -lmico2.3.12 -lmicocoss2.3.12

clean:
	-rm Alitheia.cpp Alitheia.h
	-rm Alitheia.o
	-rm main.o
	-rm logger.o
	-rm corbahandler.o
	-rm logger
