TOP_SRCDIR=../..

include $(TOP_SRCDIR)/Makefile.bundle

# Normal bundle goal for maven is 'package install'.
# Install builds the WAR a second time and there's really
# no point in having it in your m2 repo, since you never
# link against the WAR.
build :
	$(MAVEN) package

install :
	@if test "x$(WITH_TOMCAT)" = "xYES" ; then \
		echo "# Installing WAR file to $(TOMCAT_WEBAPPS)" ; \
		test -d $(TOMCAT_WEBAPPS) &&  \
		cp ../build/ROOT.war $(TOMCAT_WEBAPPS) ; \
	else echo "# Installation of webui disabled." ; fi

