all: wcmetric

main.o: main.cpp wcmetric.h
	mico-c++ -c -I../../include -o main.o main.cpp

wcmetric.o: wcmetric.cpp wcmetric.h wcmetricjob.h
	mico-c++ -c -I../../include -o wcmetric.o wcmetric.cpp

wcmetricjob.o: wcmetricjob.cpp wcmetricjob.h
	mico-c++ -c -I../../include -o wcmetricjob.o wcmetricjob.cpp

wcmetric: main.o wcmetric.o wcmetricjob.o ../../lib/libalitheia.a
	mico-ld -o wcmetric main.o wcmetric.o wcmetricjob.o -L../../lib -lalitheia -lmico2.3.12 -lmicocoss2.3.12 -lboost_thread-mt

clean:
	test ! -f main.o || rm main.o
	test ! -f wcmetric.o || rm wcmetric.o
	test ! -f wcmetricjob.o || rm wcmetricjob.o
	test ! -f wcmetric || rm wcmetric
