TOMCAT_LIB=tomcat/webapps/ROOT/WEB-INF/lib/

all : stop clean build install run

EXT_PATH=../../extlibs
EXT_LIBS=org.apache.axiom.api_1.2.2/axiom-api-1.2.2.jar \
	org.apache.axiom.dom_1.2.2/axiom-dom-1.2.2.jar \
	org.apache.axiom.impl_1.2.2/axiom-impl-1.2.2.jar \
	org.apache.axis2_1.1.0/axis2-kernel-1.1.jar \
	org.apache.axis2.databinding_1.1.0/axis2-adb-1.1.jar \
	org.apache.commons.collections_2.1.1/org.apache.commons.collections-2.1.1.jar \
	org.apache.commons.httpclient-3.0.1.jar \
	org.apache.commons.logging-1.1.1.jar \
	org.apache.ws.commons.schema_1.2.0/XmlSchema-1.2.jar \
	org.apache.wsdl4j_1.6.1/wsdl4j-1.6.1.jar \
	javax.mail.api_1.4.0/activation-1.1.jar \
	javax.mail.api_1.4.0/mail-1.4.jar \
	javax.stax.api_1.0.1/stax-api-1.0.1.jar \
	org.codehaus.stax2_3.0.1/wstx-asl-3.0.1.jar \
	org.apache.commons.codec_1.3.0/org.apache.commons.codec-1.3.0.jar 

install :
	for i in $(EXT_LIBS) ; do cp $(EXT_PATH)/$$i $(TOMCAT_LIB) || break ; done

install-scl:
	cd ../scl && $(MAKE)
	cp ../build/eu.sqooss.*.jar $(TOMCAT_LIB)

# this is where the libs live relative to the beans classes in the webui
RELATIVE_TOMCAT_LIB=../../../..
RELATIVE_SCL_LIB=$(RELATIVE_TOMCAT_LIB)/lib/eu.sqooss.scl_0.0.1.jar
RELATIVE_WS_LIB=$(RELATIVE_TOMCAT_LIB)/lib/eu.sqooss.ws.client_0.0.1.jar
BUILD_CP=$(RELATIVE_TOMCAT_LIB):$(RELATIVE_SCL_LIB):$(RELATIVE_WS_LIB)

build : install-scl
	rm -rf build
	cd tomcat/webapps/ROOT/WEB-INF/classes/eu/sqooss/webui && \
	javac -Xlint:-path -cp "$(BUILD_CP)"  *\.java

clean :
	rm -f $(TOMCAT_LIB)*
	cd ../scl && $(MAKE) clean
	rm -f tomcat/logs/*.log

run : stop start

define set_java_home
	test -z "$$JAVA_HOME" -a -x /usr/bin/javac && export JAVA_HOME=/usr ; \
	test -z "$$JAVA_HOME" -a -x /usr/local/bin/javac && export JAVA_HOME=/usr/local ;
endef

start :
	$(call set_java_home) \
	cd tomcat && \
        echo ${CLASSPATH} && \
	sh bin/catalina.sh start && \
	echo "\033[0;34m  Tomcat started: go to http://localhost:8080 to find it ...\033[1;37m"

stop :
	$(call set_java_home) \
	cd tomcat && \
	sh bin/catalina.sh stop && \
	echo "\033[0;31m  Tomcat stopped...\033[1;37m"

restart: stop run

rebuild :
	$(MAKE) build
	$(MAKE) stop && \
	$(MAKE) start 

