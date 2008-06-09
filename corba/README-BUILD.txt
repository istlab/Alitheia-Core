***
*** README-BUILD
***

This README explains how to bulid the CORBA services
Alitheia plugin.

***
*** Build the CORBA services plugin
***

The CORBA services plugin uses the same build system as the rest
of the Alitehia system is using: Maven.

To fore a complete build of the plugin run:

make clean build install

within the corba/ directory.

***
*** Build the C++ bindings
***

The C++ buildings use GNU make as build system. Cmake?

***
*** Build the Python bindings
***

As Python being a script language, it doesn't need to be compiled.
However, the IDL CORBA interface definition needs to be translated
into a Python interface. You can use omniidl for this task. This is
also done by running

make

within the python/ directory.
