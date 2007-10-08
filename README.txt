This is the Maven project tree for the Alitheia system, its components and
metrics. The top-level tree is split into the following groups:

alitheia/
    Contains the source code and build definitions for the Alitheia core and
    its components.

metrics/
    Contain the source code of the various metric plug-ins developed for the
    Alitheia system.

ui/
    Contains the source code and build definition (will probably be Maven too)
    for the public-facing website (user interface), the CLI (admin interface)
    and the eclipse plugin (IDE interface). Also contains the SCL (SQO-OSS
    connector library) which is shared between the UIs.


extlibs/
    Contains a Maven style repository of all libraries required for building
    the Alitheia system. 

***
*** Build 
*** 

See documentation in the Makefile.

***
*** Runtime configuration
***

The "make run" target launches a local Alitheia system. You may optionally
add an argument CONFIG= to the command line specifying options to pass
to the JVM. Typical use would be something like this:

	make run CONFIG="-Dlog4j.configuration=file:///tmp/l4j.config"

The following system properties are (at the very least) used:

log4j.configuration
	Set this to the URL of a log4j configuration file. The default
	configuration is contained within the logger bundle; you may override
	this by setting the configuration URL explicitly.

	The default logging sends errors to the console and logs everything
	else to a file alitheia.log in the runtime directory.

