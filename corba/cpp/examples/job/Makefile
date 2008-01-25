all: metric

main.o: main.cpp
	mico-c++ -c -I../../include -o main.o main.cpp

metric: main.o ../../lib/libalitheia.a
	mico-ld -o job main.o -L../../lib -lalitheia -lmico2.3.12 -lmicocoss2.3.12 -lboost_thread-mt

clean:
	test ! -f main.o || rm main.o
	test ! -f job || rm job
