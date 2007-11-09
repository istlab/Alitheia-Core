all : build run

build :
	cd tomcat/webapps/ROOT/WEB-INF/classes/eu/sqooss/webui && \
	javac *\.java && cd -

run : build start

define set_java_home
	test -z "$$JAVA_HOME" -a -x /usr/bin/javac && export JAVA_HOME=/usr ; \
	test -z "$$JAVA_HOME" -a -x /usr/local/bin/javac && export JAVA_HOME=/usr/local ;
endef

start :
	$(call set_java_home) \
	cd tomcat && \
	export CLASSPATH=`pwd`/webapps/ROOT/WEB-INF/classes && \
	sh bin/catalina.sh start

stop :
	$(call set_java_home) \
	cd tomcat && \
	sh bin/catalina.sh stop

restart: stop run

rebuild :
	$(MAKE) build && \ 
	make stop > /dev/null 2>&1 && echo "\033[0;31m  Tomcat stopped...\033[1;37m" && \
	make start > /dev/null 2>&1 && echo "\033[0;34m  Tomcat started...\033[1;37m"

