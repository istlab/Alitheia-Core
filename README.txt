***
*** Introduction
***

This is the source tree for the Alitheia system, its components and
metrics. The top-level tree is split into the following groups:

alitheia/
    Contains the source code and build definitions for the Alitheia core and
    its components.

metrics/
    Contain the source code of the various metric plug-ins developed for the
    Alitheia system.

tools/
    Contains build tools which were developed as part of the system.
    Shouldn't have to be packaged with the rest of the system.

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

See documentation in the Makefile. The official build system is Maven;
there is an alternative purely GNU Make-based system as well. The
short of it is:

	make clean build install run WITH_MAVEN=YES

or, for the alternative GNU Make system,

	make clean build install run

This will leave you with an OSGi console and a running Alitheia core system. 

***
*** Runtime configuration
***

The "make run" target launches a local Alitheia system. You may optionally
add an argument CONFIG= to the command line specifying options to pass
to the JVM. Typical use would be something like this:

	make run CONFIG="-Dlog4j.configuration=file:///tmp/l4j.config"

The following system properties are (at the very least) used:

[ No system properties are used at this time. ]

***
*** Runtime configuration of OSGi
***

At runtime, the OSGi framework reads properties from the file config.ini
which is located in the equinox/configuration/ directory. Use standard
key=value pairs to define properties. The supported properties are
described in the file itself.

***
*** Running tests
***

Alitheia contains a run-time testing facility. The bundle tester
(installed by default) will run, on startup, the selfTest() method
defined in each service, but only if the startup tests are enabled
in the runtime configuration file. To enable them, set

eu.sqooss.tester.enable=true

Then each service object
registered with the OSGi framework (on startup of the tester bundle)
will be examined. Those with a selfTest() method will be called.

The selfTest() method should return null on success and an Object
which describes the failure (for instance, a String) on failure.
Failures are logged. Here is a sample:

	public Object selfTest() {
		// Normal test functions return true on success
		if (actualtestFunction()) {
			return null;
		} else {
			return new String("My actualTestFunction failed.");
		}
	}

This selfTest() method must be defined in the object which is passed
to BundleContext.registerService().


***
*** Alternate logging subsystem
***

The branches/logger-log4j directory contains an alternative logger 
implementation based on Apache log4j. You may move away the alitheia/logger
directory and move in the log4j directory as it is source compatible.
Rebuild the system. This adds one runtime configuration option:

log4j.configuration
	Set this to the URL of a log4j configuration file. The default
	configuration is contained within the logger bundle; you may override
	this by setting the configuration URL explicitly.

	The default logging sends errors to the console and logs everything
	else to a file alitheia.log in the runtime directory.

