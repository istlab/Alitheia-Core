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

#### Choosing the Database Backend 
  
Alitheia Core supports two database backends: H2 and MySQL. 

**Note: The configuration should always be enabled prior to compilation.**
 
By default, Alitheia Core uses H2 (http://www.h2database.com/html/main.html) as 
its database backend. This should be ok for a local installation and experimentation, but in general it is recommended to use MySQL. 

##### Enabling Support for MySQL 
  
To enable support for MySQL the following steps must be followed: 
  
 1. Edit the file `Alitheia-­‐Core/pom.xml`: 
  
  a. Comment out the following lines:
```xml
<eu.sqooss.db>H2</eu.sqooss.db> 
<eu.sqooss.db.host>localhost</eu.sqooss.db.host>
<eu.sqooss.db.schema>alitheia;LOCK_MODE=3;MULTI_THREADED=true</eu.sqooss.db.sche
ma> 
<eu.sqooss.db.user>sa</eu.sqooss.db.user> 
<eu.sqooss.db.passwd></eu.sqooss.db.passwd> 
<eu.sqooss.db.conpool>c3p0</eu.sqooss.db.conpool> 
```
  b. Uncomment the following lines:
```xml
<eu.sqooss.db>MySQL</eu.sqooss.db>
<eu.sqooss.db.host>localhost</eu.sqooss.db.host>
<eu.sqooss.db.schema>alitheia</eu.sqooss.db.schema> 
<eu.sqooss.db.user>alitheia</eu.sqooss.db.user> 
<eu.sqooss.db.passwd>alitheia</eu.sqooss.db.passwd> 
<eu.sqooss.db.conpool>c3p0</eu.sqooss.db.conpool> 
``` 
 2. Edit the MySQL main configuration file (usually named `/etc/my.cnf`) and add the 
  following lines: 
``` 
default-­‐storage-­‐engine=innodb 
transaction_isolation=READ-­‐COMMITTED
```
The above lines enable innodb as default.
  
 3. Create an empty database named `alitheia`. Then create a database user named 
  
`alitheia` with password `alitheia` and grant full control over the database 
(@localhost). 
#### Building the project

* To build the project, do:

  `mvn install`

* To run Alitheia Core, you need to configure a database first. Consult the
  online Quickstart guide at http://www.sqo-oss.org/quickstart
  on how to do it. Then, run:

  `mvn pax:provision`

  Then visit the web interface: [http://localhost:8080](http://localhost:8080)

* To debug Alitheia Core (on Unix-based systems):
```
  mvn install pax:provision
  [quit the OSGi prompt]
  ./debug.sh
```
  Then you can connect a remote Java debugger to port 8000 on localhost.

* To develop for Alitheia Core:

  * To develop a metric plug-in: see metrics/README.txt
  * To develop a data plug-in: see plug-ins/README.txt

  Any tool can be used to write code for Alitheia Core, but using Eclipse will
  simplify things a lot. Instructions can be found in README.eclipse.


  
