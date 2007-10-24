all : build run

build :
	cd tomcat/webapps/ROOT/WEB-INF/classes/sqo && \
	$(MAKE)

run : start

build :
	PWD=`pwd` && \
	echo ${PWD} && \
	cd tomcat/webapps/ROOT/WEB-INF/classes/sqo && \
	make clean && \
	make build && \
	cd ${PWD}

start :
	make build && \
	cd tomcat && \
	export CLASSPATH=tomcat/webapps/ROOT/WEB-INF/classes && \
	sh bin/catalina.sh start

stop :
	cd tomcat && \
	sh bin/catalina.sh stop

restart : stop start

