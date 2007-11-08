all : build run

build :
	cd tomcat/webapps/ROOT/WEB-INF/classes/eu/sqooss/webui && \
	javac *\.java && cd -

run : build start

start :
	if test -z $$JAVA_HOME ; then \
		test -x /usr/bin/javac && export JAVA_HOME=/usr ; \
	fi ; \
	cd tomcat && \
	export CLASSPATH=tomcat/webapps/ROOT/WEB-INF/classes && \
	sh bin/catalina.sh start

stop :
	if test -z $$JAVA_HOME ; then \
		test -x /usr/bin/javac && export JAVA_HOME=/usr ; \
	fi ; \
	cd tomcat && \
	sh bin/catalina.sh stop

restart: stop run

rebuild :
	$(MAKE) build && \ 
	make stop > /dev/null 2>&1 && echo "\033[0;31m  Tomcat stopped...\033[1;37m" && \
	make start > /dev/null 2>&1 && echo "\033[0;34m  Tomcat started...\033[1;37m"

