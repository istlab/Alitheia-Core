***
*** README-DEVELOP
***

This README explains how the Alitheia coding styles are set up, how the
Continuous Integration system works, and how to write code in Alitheia
using any of three development environments:

- editor + terminal (traditional UNIX)
- Eclipse
- Netbeans

***
*** Coding style
***

The coding style for Alitheia follows the Sun Java coding guidelines.
These are documented at

 http://java.sun.com/docs/codeconv/
 http://java.sun.com/docs/codeconv/CodeConventions.pdf
 http://istlab.dmst.aueb.gr/~george/labs/stuff/java-progr-style.pdf

As usual, following the style of the existing source files is preferable to
adhering too strictly to the rules.

Each file should start with an appropriate license comment. Indent is
four spaces; no tabs are used. Files should have native EOL style.
The file Template.java.in has the right SVN settings, license comment
and vim modeline for use.

***
*** Traditional UNIX development
***

The coding style is amenable to both vi and emacs users. Files should
have a modeline to set up the correct environment for traditional editors.
Most subdirectories have a Makefile to build the sources underneath
that subdirectory.

The biggest challenge is dealing with conventional Java source code
organisation, which uses many deep directories. After a while,
command lines like

	vi src/eu/sqooss/impl/service/fds/FDSServiceImpl.java

become tedious. Especially since the sources are spread out across
a tree with many branches under src/eu/sqooss inside each directory.

TODO: Add an emacs style setting to our SVN.

***
*** Netbeans Development
***

Use Netbeans 6 or later. Install the Mevenide plugin (you can find it at
http://mevenide.codehaus.org/m2-site/index.html). With the Mevenide plugin
you can open any Maven project as a Netbeans project. For instance, in
the Open Project dialog you can select devel/trunk as the project to open,
tick 'Open Required Projects' to pull in the sub-projects and Netbeans
will load all of the sources for the Alitheia Core Platform in one go.

NOTE: You must use the make-based build system at least once to initialize
Maven, get the required libraries, etc. Run

	make clean build install

once before loading Alitheia as a Mevenide based Netbeans project.

***
*** Eclipse Development
***
There are currently 2 methods to develop on SQO-OSS using Eclipse, listed below
in order of ease of use and development effectiveness:
 1. As a source editor & debugger using cmd-line based builds
 2. Using the Maven integration plug-in to handle builds and depedencies

1. Using Eclipse as a source editor and debugger
--------------------------------------

Checkout the devel/trunk directory to a convenient location. Open
Eclipse, and then select File->New-> Java Project. Give the project a
name and select the Create project from existing source radio
button. Browse to the dir where the local sqo-oss check out resides
and click Next. Eclipse will try to import all paths containing source
code, unfortunately a little too hard.  In the next screen (Java build
path), remove the corba/* and ui/eclipse/* source folders. After those
steps, you should have a project that Eclipse can compile. If you have
the SVN plug-in installed, you can use it to perform SVN related
actions in Eclipse. 

To build SQO-OSS use the comand line build tools. To debug, use the
start-core-debug target. The JVM will start in pause mode. Use the
Remote Java Application option from the Debug Configuration dialog in
Eclipse to create a SQO-OSS debug configuration. Eclipse will then
connect to the JVM instance, upause it and switch to the debug
prespective.  From there, debug can proceed as usual.


2. Using the Eclipse Maven integration plugin
------------------------------------

As of this writting, the integration of Maven within the Eclipse
environment is not on par with other build systems. Use
Eclipse 3.3 or later. Install the Maven plugin from 

http://m2eclipse.codehaus.org/

Checkout the devel/trunk directory to a convenient location. Open Eclipse
and then choose File->Import->Maven Projects. Open the directory containing
the SQO-OSS source code and choose to import all .poms recognised. After a
while, you will find that Eclipse has created a project for each pom and that it
tried to configure the build paths. Not everything is setup correctly though, 
minor corrections, such as setting the correct source code path the
created projects, must be made by hand. Eclipse will then be able to compile 
SQO-OSS using maven and will also be able to resolve depedencies and edit 
.poms using editor autocompletion


