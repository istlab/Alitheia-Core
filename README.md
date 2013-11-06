## Introduction


This is the source tree for Alitheia Core, a platform for 
software analytics and software engineering research.

The README gives an overview of how the source tree is organised and sort
instructions on how to use and develop for Alitheia Core. More documentation
is always available at the project's web site at

http://www.sqo-oss.org


### Source Organisation

You can find the following files and directories here:

`LICENSE`
    These are the licenses that apply to the software included in this
    source tree. The LICENSE file applies to Alitheia itself, and is the
    2-clause BSD license. Other parts of the system may have different
    licenses, and these are contained in separate files. Many software
    components used in (or by) Alitheia fall under that Apache License,
    version 2.

`pom.xml`
    As any other modern Java project, Alitheia Core uses maven for building. 
    We also use the Pax OSGi tools. You need to have those installed if
    you plan to add functionality or external libraries to Alitheia Core

`alitheia/`
    Contains the Alitheia Core source code (under core/) and modules for the
    programmating interfaces to the system.

`external/`
    External libraries (actually library references) to be bundled as OSGi
    bundles at complile time.

`metrics/`
    Contains the source code of the various metric plug-ins developed for the
    Alitheia system. Each metric is a self-contained codebase.

`plug-ins/`
    Code for data and updater plug-ins.

`ui/`
    Source code for a simple client to the Alitheia Core REST api.

### Getting the code

The source code to Alitheia Core is maintained on Github, using the Git
version control system. To get the code (including development history)
Git must be installed. Git can be downloaded from: http://git-scm.com/download

Without a Github account, the code can be checked out as follows:

git clone git://github.com/istlab/Alitheia-Core.git

Courtesy of Github, a zip of the current latest version of the software
can be downloaded.

https://github.com/istlab/Alitheia-Core/zipball/master

### Compiling, running and developing

Alitheia Core is build using Maven (tested with version > 3). You can download
Maven from the following link: http://maven.apache.org/

* To build the project, do:

  `mvn install`

* To run Alitheia Core, you need to configure a database first. Consult the
  online Quickstart guide at http://www.sqo-oss.org/quickstart
  on how to do it. Then, run:

  `mvn pax:provision`

  Then visit the web interface: [http://localhost:8080](http://localhost:8080)

* To debug Alitheia Core (on Unix-based systems):
```
  mvn install pax:provision`
  [quit the OSGi prompt]
  ./debug.sh
```
  Then you can connect a remote Java debugger to port 8000 on localhost.

* To develop for Alitheia Core:

  * To develop a metric plug-in: see metrics/README.txt
  * To develop a data plug-in: see plug-ins/README.txt

  Any tool can be used to write code for Alitheia Core, but using Eclipse will
  simplify things a lot. Instructions can be found in README.eclipse

