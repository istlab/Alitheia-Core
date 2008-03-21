***
*** Introduction
***

This is the source tree for the software produced by the SQO-OSS project.
The main software product is Alitheia, the platform for the automated
objective measurement of the quality of Open Source software projects.

The README gives an overview of how the source tree is organised and
how the rest of the READMEs can be used.

***
*** System organisation
***

An Alitheia deployment consists of at least three software systems:

- The File System back-end, which stores actual project data. This will
  usually be a mirror of actual project data stores (to be nice to the
  projects under study).

- The Alitheia Core Platform, also known as "the cruncher" which does
  all of the calculations and data-processing for the system.

- The Alitheia UI (there are several) which is what the user interacts
  with; the UI uses web-services to communicate with the cruncher.


The back-end is not a single software product, but is cobbled together
from bits and pieces; some of these bits may be found in the tools/
source directory. The UI parts are found in the ui/ directory and 
the rest lives in the other source directories which you may find here.

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

Makefile*
    This is part of the build system for the Alitheia software. See
    the section "Build System", in the file README-BUILD.txt

README*
    These files contain high-level documentation for the source code,
    such as how to build it, how to install it, and how to run it.

Template.java.in
    This single file is used as a template for our Java sources.

pom.xml
    This is part of the build system, see the file README-BUILD.txt

alitheia/
    Contains the source code the Alitheia platform.

corba/
    Contains the Corba / C++ bindings for the Alitheia platform. 

equinox/
    Contains deployment configuration and is the default installation
    path when building the Alitheia software in development.

examples/
    Contains sample data for an Alitheia deployment, including
    project data, measurements, and possibly mail archives.

metrics/
    Contain the source code of the various metric plug-ins developed for the
    Alitheia system. Each metric is a self-contained codebase.

sharedlibs/
    Code which is shared between the UI and the Core Platform is
    stored here and built first.

sqoossrepo/
    This is a Maven-style repository for all of the dependencies
    that Alitheia has. The intention is that pristine source goes
    in here, which is bundlized on the fly for use in the Alitheia
    system (but this is not so -- many are packaged in the repo
    already in bundlized form).

tools/
    Contains build tools which were developed as part of the system.
    Shouldn't have to be packaged with the rest of the system.

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

