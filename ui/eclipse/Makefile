TOP_SRCDIR=../..

include $(TOP_SRCDIR)/Makefile.bundle

clean-dir :
	rm -rf sqoosslib

PLUGINS_FOLDER = "/plugins"
DESTINATION_FOLDER = "$(ECLIPSE_HOME)$(PLUGINS_FOLDER)"
install :
	$(MAVEN) assembly:directory package
	@if test "$(DESTINATION_FOLDER)" != "$(PLUGINS_FOLDER)" ; then \
		echo "# Installing the Eclipse IDE plug-in to $(DESTINATION_FOLDER)" ; \
		test -d $(DESTINATION_FOLDER) && \
		cp ../build/eu.sqooss.ui.eclipse-1.0.0.jar $(DESTINATION_FOLDER) ; \
	else echo "# The ECLIPSE_HOME variable is not initialized!" ; fi

