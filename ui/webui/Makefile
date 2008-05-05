TOP_SRCDIR=../..

include $(TOP_SRCDIR)/Makefile.bundle

# Normal bundle goal for maven is 'package install'.
# Install builds the WAR a second time and there's really
# no point in having it in your m2 repo, since you never
# link against the WAR.
build :
	$(MAVEN) package

# How to use the SQO-OSS web ui:
#
# - download tomcat 6 ( http://apache.cs.uu.nl/dist/tomcat/tomcat-6/v6.0.16/bin/apache-tomcat-6.0.16.tar.gz )
# - unpack that tarball somewhere. Call that directory D.
# - check that $D/apache-tomcat-6.0.16 exists.
# - add a line 'WITH_TOMCAT=YES' to your Makefile.config.local.
# - add a line 'TOMCAT_BASE=$D/apache-tomcat-6.0.16' (with D equal to 
#   the directory where you unpacked tomcat to Makefile.config.local.
# - clean up tomcat
#	$ cd $D/apache-tomcat-6.0.16
#	$ rm -rf work/Catalina/localhost/ webapps/ROOT
# - start tomcat, for instance
#	$ cd $D/apache-tomcat-6.0.16
#	$ sh bin/catalina.sh start
# - hit localhost:8080 (default configuration) to see the tomcat start page
#
# Now you can just leave tomcat running.
#
# - in webui, 'make build install' will update the JAR and deploy it
#   to tomcat; hit reload in your web browser (once, maybe twice) to
#   get tomcat to recompile.
#
install :
	@if test "x$(WITH_TOMCAT)" = "xYES" ; then \
		echo "# Installing WAR file to $(TOMCAT_WEBAPPS)" ; \
		test -d $(TOMCAT_WEBAPPS) &&  \
		cp ../build/ROOT.war $(TOMCAT_WEBAPPS) ; \
	else echo "# Installation of webui disabled." ; fi

restart :
	${TOMCAT_WEBAPPS}../bin/catalina.sh stop
	${TOMCAT_WEBAPPS}../bin/catalina.sh start
