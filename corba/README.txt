***
*** README for CORBA services
***

The readme consists of three other pieces:

README-RUN.txt describes how to run the CORBA services plugin within
Alitheia.

README-DEVELOP.txt describes how to develop CORBA based Alitheia
plugins.

README-BUILD.txt contains information on how to build the CORBA plugin.

***
*** Source Organisation
***

You can find the following files and directories here:

Makefile
    This is part of the build system for Alitheia software. See
    the section "Build System", in the file README-BUILD.txt

README*
    These files contain documentation about the different pieces of
    this plugin. How to build them, how to run them and how to develop
    metrics with the plugin.

pom.xml
    This is part of the build system, see the file README-BUILD.txt

corbaservice/
    Contains the Alitheia plugin which exports the different functionalities
    into the CORBA ORB.

cpp/
    Contains a convenient C++ API for developing metrics with the CORBA
    services plugin.

idl/
    Contains the CORBA interface definition. You can use the IDL file to
    develop other language bindings than C++ and Python.

python/
    Contains a convenient Python API for developing metrics with the CORBA
    services plugin.

