all : build run

build :
	CLASSPATH=/home/sebas/work/svn.sqo-oss.eu/devel/trunk/ui/scl/src:/home/sebas/work/svn.sqo-oss.eu/devel/trunk/ui/webui/tomcat/webapps/ROOT/WEB-INF/classes && \
	cd tomcat/webapps/ROOT/WEB-INF/classes/eu/sqooss/webui && \
	javac -Xlint:path -cp ":../../../../lib/eu.sqooss.scl_0.0.1.jar" *\.java && \
	cd - && \
        cp ../../extlibs/org.apache.axiom.api_1.2.2/axiom-api-1.2.2.jar tomcat/webapps/ROOT/WEB-INF/lib/ && \
        cp ../../extlibs/org.apache.axiom.dom_1.2.2/axiom-dom-1.2.2.jar tomcat/webapps/ROOT/WEB-INF/lib/ && \
        cp ../../extlibs/org.apache.axiom.impl_1.2.2/axiom-impl-1.2.2.jar tomcat/webapps/ROOT/WEB-INF/lib/ && \
        cp ../../extlibs/org.apache.axis2_1.1.0/axis2-kernel-1.1.jar tomcat/webapps/ROOT/WEB-INF/lib/ && \
        cp ../../extlibs/org.apache.axis2.databinding_1.1.0/axis2-adb-1.1.jar tomcat/webapps/ROOT/WEB-INF/lib/ && \
        cp ../../extlibs/org.apache.commons.collections_2.1.1/org.apache.commons.collections-2.1.1.jar tomcat/webapps/ROOT/WEB-INF/lib/ && \
        cp ../../extlibs/org.apache.commons.httpclient-3.0.1.jar tomcat/webapps/ROOT/WEB-INF/lib/ && \
        cp ../../extlibs/org.apache.commons.logging-1.1.1.jar tomcat/webapps/ROOT/WEB-INF/lib/ && \
        cp ../../extlibs/org.apache.ws.commons.schema_1.2.0/XmlSchema-1.2.jar tomcat/webapps/ROOT/WEB-INF/lib/ && \
        cp ../../extlibs/org.apache.wsdl4j_1.6.1/wsdl4j-1.6.1.jar tomcat/webapps/ROOT/WEB-INF/lib/ && \
        cp ../../extlibs/javax.mail.api_1.4.0/activation-1.1.jar tomcat/webapps/ROOT/WEB-INF/lib/ && \
        cp ../../extlibs/javax.mail.api_1.4.0/mail-1.4.jar tomcat/webapps/ROOT/WEB-INF/lib/ && \
        cp ../../extlibs/javax.stax.api_1.0.1/stax-api-1.0.1.jar tomcat/webapps/ROOT/WEB-INF/lib/ && \
        cp ../../extlibs/org.codehaus.stax2_3.0.1/wstx-asl-3.0.1.jar tomcat/webapps/ROOT/WEB-INF/lib/ && \
	cd -

run : build start

define set_java_home
	test -z "$$JAVA_HOME" -a -x /usr/bin/javac && export JAVA_HOME=/usr ; \
	test -z "$$JAVA_HOME" -a -x /usr/local/bin/javac && export JAVA_HOME=/usr/local ;
endef

start :
	$(call set_java_home) \
	cd tomcat && \
        echo ${CLASSPATH} && \
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

