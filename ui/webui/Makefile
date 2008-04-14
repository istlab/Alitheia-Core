TOP_SRCDIR=../..

include $(TOP_SRCDIR)/Makefile.bundle

install :
	@if test "x$(WITH_TOMCAT)" = "xYES" ; then \
		echo "# Installing WAR file to $(TOMCAT_WEBAPPS)" ; \
		test -d $(TOMCAT_WEBAPPS) &&  \
		cp ../build/ROOT.war $(TOMCAT_WEBAPPS) ; \
	else echo "# Installation of webui disabled." ; fi

