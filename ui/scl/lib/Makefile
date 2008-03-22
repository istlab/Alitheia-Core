TOP_SRCDIR=../../..
include $(TOP_SRCDIR)/Makefile.bundle

fetch-wsdl : clean-wsdl ws.wsdl

clean-wsdl :
	rm -f ws.wsdl

ws.wsdl :
	java -cp ../../../tools/GetHTML.jar GetHTML "http://localhost:8088/sqooss/services/ws?wsdl" ws.wsdl

.PHONY : clean-wsdl

EXT_PATH=$(PREFIX)
EXT_LIBS=org.apache.axis2_1.1.0/axis2-kernel-1.1.jar \
	org.apache.wsdl4j_1.6.1/wsdl4j-1.6.1.jar \
	org.apache.commons.logging-1.1.1.jar \
	org.apache.neethi_2.0.0/neethi-2.0.jar \
	org.apache.ws.commons.schema_1.2.0/XmlSchema-1.2.jar \
	org.apache.axiom.api_1.2.2/axiom-api-1.2.2.jar \
	org.apache.axis2.databinding_1.1.0/axis2-adb-1.1.jar \
	javax.mail.api_1.4.0/activation-1.1.jar
CODEGEN_PATH=$(HOME)/Research/sqo/tools/javatools/wsdl2java
CODEGEN_LIBS=axis2-adb-codegen-1.1.jar axis2-codegen-1.1.jar

$(eval $(call build_classpath,WSDL_CLASSPATH,EXT_LIBS,EXT_PATH))
$(eval $(call build_classpath,WSDL_CLASSPATH,CODEGEN_LIBS,CODEGEN_PATH))

compile-wsdl : ws.wsdl
	java -cp $(WSDL_CLASSPATH) org.apache.axis2.wsdl.WSDL2Java \
		-uri ws.wsdl -s -u -o src.new \
		-p eu.sqooss.ws.client \
		-ns2p http://datatypes.services.web.service.impl.sqooss.eu/xsd=eu.sqooss.ws.client.datatypes,http://services.web.service.sqooss.eu/xsd=eu.sqooss.ws.client.ws 


install-wsdl :
	( cd src.new && tar cf - src ) | tar xvf - 

