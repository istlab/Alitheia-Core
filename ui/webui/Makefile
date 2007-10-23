all : build run

build :
	cd tomcat/webapps/ROOT/WEB-INF/classes/sqo && \
	$(MAKE)

run : start

start :
	cd tomcat && \
	export CLASSPATH=tomcat/webapps/ROOT/WEB-INF/classes && \
	sh bin/catalina.sh start

stop :
	cd tomcat && \
	sh bin/catalina.sh stop

restart : stop start

