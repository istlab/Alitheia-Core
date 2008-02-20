all : build-all

include ../../../Makefile.common

EXT_PATH=../../../extlibs
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

$(eval $(call build_classpath,CPLIBS,EXT_LIBS,EXT_PATH))

SOURCES=$(shell find src/ -name '*.java')

MANIFEST=manifest/ws-client.mf
BUILD_DIR=build/

DEPLOY_DIR=../../build/
DEPLOY_VER=0.0.1
DEPLOY_NAME=eu.sqooss.ws.client_$(DEPLOY_VER).jar

build-all : build $(BUILD_DIR)$(DEPLOY_NAME)

build :
	test -d $(BUILD_DIR) || mkdir $(BUILD_DIR)

$(BUILD_DIR)$(DEPLOY_NAME) : $(SOURCES)
	test -d $(BUILD_DIR)
	javac $(JAVA_LINT) -d $(BUILD_DIR) -cp $(CPLIBS) $(SOURCES)
	jar cfm $(BUILD_DIR)$(DEPLOY_NAME) $(MANIFEST) -C $(BUILD_DIR) eu

clean:
	rm -rf $(BUILD_DIR)
	rm -rf $(DEPLOY_DIR)$(DEPLOY_NAME)

install: build-all
	test -d $(DEPLOY_DIR) || mkdir $(DEPLOY_DIR)
	cp $(BUILD_DIR)$(DEPLOY_NAME) $(DEPLOY_DIR)

fetch-wsdl :
	rm -f ws.wsdl
	java -cp ../../../tools/GetHTML.jar GetHTML "http://localhost:8088/sqooss/services/ws?wsdl" ws.wsdl

EXT_PATH=../../../extlibs
EXT_LIBS=org.apache.axis2_1.1.0/axis2-kernel-1.1.jar \
	org.apache.wsdl4j_1.6.1/wsdl4j-1.6.1.jar \
	org.apache.commons.logging-1.1.1.jar \
	org.apache.neethi_2.0.0/neethi-2.0.jar \
	org.apache.ws.commons.schema_1.2.0/XmlSchema-1.2.jar \
	org.apache.axiom.api_1.2.2/axiom-api-1.2.2.jar \
	org.apache.axis2.databinding_1.1.0/axis2-adb-1.1.jar \
	javax.mail.api_1.4.0/activation-1.1.jar
CODEGEN_PATH=../../../../../tools/javatools/wsdl2java
CODEGEN_LIBS=axis2-adb-codegen-1.1.jar axis2-codegen-1.1.jar

$(eval $(call build_classpath,WSDL_CLASSPATH,EXT_LIBS,EXT_PATH))
$(eval $(call build_classpath,WSDL_CLASSPATH,CODEGEN_LIBS,CODEGEN_PATH))

compile-wsdl :
	java -cp $(WSDL_CLASSPATH) org.apache.axis2.wsdl.WSDL2Java \
		-uri ws.wsdl -s -u -o src.new \
		-p eu.sqooss.ws.client \
		-ns2p http://datatypes.services.web.service.impl.sqooss.eu/xsd=eu.sqooss.ws.client.datatypes,http://services.web.service.sqooss.eu/xsd=eu.sqooss.ws.client.ws 



