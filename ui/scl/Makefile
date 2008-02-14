EXT_PATH=../../extlibs
WSC_LIB=../build/eu.sqooss.ws.client_0.0.1.jar

CPLIBS=
CPLIBS:=$(CPLIBS):$(EXT_PATH)/org.apache.axiom.api_1.2.2/axiom-api-1.2.2.jar
CPLIBS:=$(CPLIBS):$(EXT_PATH)/org.apache.axiom.dom_1.2.2/axiom-dom-1.2.2.jar
CPLIBS:=$(CPLIBS):$(EXT_PATH)/org.apache.axiom.impl_1.2.2/axiom-impl-1.2.2.jar
CPLIBS:=$(CPLIBS):$(EXT_PATH)/org.apache.axis2_1.1.0/axis2-kernel-1.1.jar
CPLIBS:=$(CPLIBS):$(EXT_PATH)/org.apache.axis2.databinding_1.1.0/axis2-adb-1.1.jar
CPLIBS:=$(CPLIBS):$(EXT_PATH)/org.apache.commons.collections_2.1.1/org.apache.commons.collections-2.1.1.jar
CPLIBS:=$(CPLIBS):$(EXT_PATH)/org.apache.commons.httpclient-3.0.1.jar
CPLIBS:=$(CPLIBS):$(EXT_PATH)/org.apache.commons.logging-1.1.1.jar
CPLIBS:=$(CPLIBS):$(EXT_PATH)/org.apache.ws.commons.schema_1.2.0/XmlSchema-1.2.jar
CPLIBS:=$(CPLIBS):$(EXT_PATH)/org.apache.wsdl4j_1.6.1/wsdl4j-1.6.1.jar
CPLIBS:=$(CPLIBS):$(EXT_PATH)/javax.mail.api_1.4.0/activation-1.1.jar
CPLIBS:=$(CPLIBS):$(EXT_PATH)/javax.mail.api_1.4.0/mail-1.4.jar
CPLIBS:=$(CPLIBS):$(EXT_PATH)/javax.stax.api_1.0.1/stax-api-1.0.1.jar
CPLIBS:=$(CPLIBS):$(EXT_PATH)/org.codehaus.stax2_3.0.1/wstx-asl-3.0.1.jar
CPLIBS:=$(CPLIBS):$(EXT_PATH)/org.apache.commons.codec_1.3.0/org.apache.commons.codec-1.3.0.jar
CPLIBS:=$(CPLIBS):$(WSC_LIB)

SOURCES=`find src/ | grep \.java$$`

MANIFEST=META-INF/MANIFEST.MF
BUILD_DIR=build/

DEPLOY_DIR=../build/
DEPLOY_VER=0.0.1
DEPLOY_NAME=eu.sqooss.scl_$(DEPLOY_VER).jar

all : install

build : ws-client
	mkdir $(BUILD_DIR) && \
	javac -Xlint:-path -d $(BUILD_DIR) -cp $(CPLIBS) $(SOURCES)

clean:
	cd lib && $(MAKE) clean
	rm -rf $(BUILD_DIR) &&\
	rm -rf $(DEPLOY_DIR)$(DEPLOY_NAME)

install: ws-client build
	jar cfm $(DEPLOY_DIR)$(DEPLOY_NAME) $(MANIFEST) -C $(BUILD_DIR) .

ws-client :
	cd lib && $(MAKE) install


