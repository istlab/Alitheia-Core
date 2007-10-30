all : build run

build :
	cd tomcat/webapps/ROOT/WEB-INF/classes/eu/sqooss/webui && \
	$(MAKE)

run : start

start :
	if test -z $$JAVA_HOME ; then \
		test -x /usr/bin/javac && export JAVA_HOME=/usr ; \
	fi ; \
	make build && \
	cd tomcat && \
	export CLASSPATH=tomcat/webapps/ROOT/WEB-INF/classes && \
	sh bin/catalina.sh start

stop :
	if test -z $$JAVA_HOME ; then \
		test -x /usr/bin/javac && export JAVA_HOME=/usr ; \
	fi ; \
	cd tomcat && \
	sh bin/catalina.sh stop

restart : stop start

