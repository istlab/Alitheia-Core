all: examples

build: examples

src:
	${MAKE} -C src
	
examples: src
	${MAKE} -C examples

clean:
	${MAKE} -C src clean
	${MAKE} -C examples clean
