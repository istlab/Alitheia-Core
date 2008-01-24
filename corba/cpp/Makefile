all: examples

idl:
	${MAKE} -C ../idl
	
src: idl
	${MAKE} -C src
	
examples: src
	${MAKE} -C examples

clean:
	${MAKE} -C src clean
	${MAKE} -C examples clean
