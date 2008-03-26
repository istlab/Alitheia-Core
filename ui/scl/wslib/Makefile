TOP_SRCDIR=../../..
include $(TOP_SRCDIR)/Makefile.bundle

fetch-wsdl : clean-wsdl ws.wsdl

clean-wsdl :
	rm -f ws.wsdl

ws.wsdl :
	java -cp $(TOP_SRCDIR)/tools/GetHTML.jar GetHTML "http://localhost:8088/sqooss/services/ws?wsdl" ws.wsdl

CODEGEN_PATH=$(TOP_SRCDIR)/tools
CODEGEN_LIBS=axis2-adb-codegen-1.1.jar axis2-codegen-1.1.jar

EXT_CP=$(foreach d,$(wildcard target/dependency/*.jar),$(d))
APP_CP=$(foreach d,$(CODEGEN_LIBS),$(CODEGEN_PATH)/$(d))
ALL_CP=$(shell echo $(EXT_CP) $(APP_CP) | tr ' ' ':')

compile-wsdl : ws.wsdl
	java -cp $(ALL_CP) org.apache.axis2.wsdl.WSDL2Java \
		-uri ws.wsdl -s -u -o src.new \
		-p eu.sqooss.ws.client \
		-ns2p http://datatypes.services.web.service.impl.sqooss.eu/xsd=eu.sqooss.ws.client.datatypes,http://services.web.service.sqooss.eu/xsd=eu.sqooss.ws.client.ws 


install-wsdl :
	( cd src.new && tar cf - src ) | tar xvf - 

install-deps :
	$(MAVEN) dependency:copy-dependencies

