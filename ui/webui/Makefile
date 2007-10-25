all : build run

build :
	cd tomcat/webapps/ROOT/WEB-INF/classes/eu/sqooss/webui && \
	$(MAKE)

run : start

start :
	make build && \
	cd tomcat && \
	export CLASSPATH=tomcat/webapps/ROOT/WEB-INF/classes && \
	sh bin/catalina.sh start

stop :
	cd tomcat && \
	sh bin/catalina.sh stop

restart : stop start

