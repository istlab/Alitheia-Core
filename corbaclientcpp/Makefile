all: logger

Alitheia.cpp: Alitheia.idl
	idl --poa --c++-suffix cpp Alitheia.idl

Alitheia.o: Alitheia.cpp
	mico-c++ -I. -c Alitheia.cpp

logger.o: Alitheia.cpp logger.cpp
	mico-c++ -c logger.cpp

logger: logger.o Alitheia.o
	mico-ld -o logger logger.o Alitheia.o -lmico2.3.12 -lmicocoss2.3.12

clean:
	-rm Alitheia.cpp Alitheia.h
	-rm Alitheia.o
	-rm logger.o
	-rm logger
