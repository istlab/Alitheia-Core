***
*** Introduction
***

This is the source tree for the software produced by the SQO-OSS project.
The main software product is Alitheia Core, the platform for the automated
measurement of the quality of  projects.

The README gives an overview of how the source tree is organised and
how the rest of the READMEs can be used.

***
*** Source Organisation
***

You can find the following files and directories here:

LICENSE*
    These are the licenses that apply to the software included in this
    source tree. The LICENSE file applies to Alitheia itself, and is the
    2-clause BSD license. Other parts of the system may have different
    licenses, and these are contained in separate files. Many software
    components used in (or by) Alitheia fall under that Apache License,
    version 2.

pom.xml
    As any other modern Java project, Alitheia Core uses maven for building. 
    We also use the Pax OSGi tools. You need to have those installed if
    you plan to add functionality or external libraries to Alitheia Core

alitheia/
    Contains the Alitheia Core source code (under core/) and modules for the
    programmating interfaces to the system. 

metrics/
    Contain the source code of the various metric plug-ins developed for the
    Alitheia system. Each metric is a self-contained codebase.

tools/
    Contains build and data mirroring tools which were developed as part of the
    system.  Shouldn't have to be packaged with the rest of the system.

ui/
    Contains the source code and build definition (will probably be Maven too)
    for the public-facing website (user interface), the CLI (admin interface)
    and the eclipse plugin (IDE interface). Also contains the SCL (SQO-OSS
    connector library) which is shared between the UIs.

***
*** Using Alitheia
***

To read about building the system, see README-BUILD.txt
To read about configuring the system for use, see README-CONFIGURE.txt
To read about running the system, see README-RUN.txt
To read about development practices and how to code, see README-DEVELOP.txt

