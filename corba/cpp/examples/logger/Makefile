all: logger

main.o: main.cpp
	mico-c++ -c -I../../include -o main.o main.cpp

logger: main.o
	mico-ld -o logger main.o -L../../lib -lalitheia -lmicocoss2.3.12 -lmicocoss2.3.12

clean:
	-rm main.o
	-rm logger
