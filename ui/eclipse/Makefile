TOP_SRCDIR=../..

include $(TOP_SRCDIR)/Makefile.bundle

clean-dir :
	rm -rf sqoosslib
	rm -rf ../build

# How to install and use the Eclipse IDE plug-in:
# 1. Install instructions
#   1.1 Download the Eclipse IDE from the official web site (www.eclipse.org)
#    1.2 Unpack the downloaded archive in some directory (for example: eclipse).
#    1.3 Set the environment variable $ECLIPSE_HOME=<path_to_eclipse>/eclipse
#    1.4 Use make clean install to build and install the plug-in
# 2. Usage instructions
#   2.1 Open the "Properties" menu (Alt+Enter) of the resource (project, file, ...)
#       for example: You can select the project in the Package Explorer view and open the properties menu.
#                    There are the SQO-OSS property pages.

PLUGINS_FOLDER = "/plugins"
DESTINATION_FOLDER = "$(ECLIPSE_HOME)$(PLUGINS_FOLDER)"
install :
	$(MAVEN) assembly:directory package
	@if test "$(DESTINATION_FOLDER)" != "$(PLUGINS_FOLDER)" ; then \
		echo "# Installing the Eclipse IDE plug-in to $(DESTINATION_FOLDER)" ; \
		test -d $(DESTINATION_FOLDER) && \
		cp ../build/eu.sqooss.ui.eclipse-1.0.0.jar $(DESTINATION_FOLDER) ; \
	else echo "# The ECLIPSE_HOME variable is not initialized!" ; fi

