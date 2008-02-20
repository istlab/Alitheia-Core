
# These are the web-service libraries built as part of the Alitheia build.
WSC_LIBS=../build/eu.sqooss.ws.client_0.0.1.jar

# These are external libraries (which live under EXT_PATH).
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

# Build up a set of paths for the class path
$(foreach d,$(EXT_LIBS),$(eval EXTRA_CP:=$(EXTRA_CP)$(EXT_PATH)/$(d):))
EXTRA_CP:=$(EXTRA_CP)$(WSC_LIBS)

# Adjust for Windows
ifeq ($(OS),Windows_NT)
EXTRA_CP:=$(subst /,\,$(EXTRA_CP))
EXTRA_CP:=$(subst :,;,$(EXTRA_CP))
endif
CLASSPATH=$(EXTRA_CP)

SOURCES=$(shell find src/ -name '*.java')

MANIFEST=manifest/scl.mf
BUILD_DIR=build/

DEPLOY_DIR=../build/
DEPLOY_VER=0.0.1
DEPLOY_NAME=eu.sqooss.scl_$(DEPLOY_VER).jar

all : install

build : build-ws-client build-scl


build-ws-client :
	cd lib && $(MAKE) install

build-scl : $(BUILD_DIR)$(DEPLOY_NAME)

$(BUILD_DIR)$(DEPLOY_NAME) : $(SOURCES)
	test -d $(BUILD_DIR) || mkdir $(BUILD_DIR)
	test -d $(BUILD_DIR) && \
	javac -Xlint:-path -d $(BUILD_DIR) -cp $(CLASSPATH) $(SOURCES)
	jar cfm $(BUILD_DIR)$(DEPLOY_NAME) $(MANIFEST) -C $(BUILD_DIR) eu

clean:
	cd lib && $(MAKE) clean
	rm -rf $(BUILD_DIR) &&\
	rm -rf $(DEPLOY_DIR)$(DEPLOY_NAME)

install: build
	test -d $(DEPLOY_DIR) || mkdir $(DEPLOY_DIR)
	cp $(BUILD_DIR)$(DEPLOY_NAME) $(DEPLOY_DIR)$(DEPLOY_NAME)


