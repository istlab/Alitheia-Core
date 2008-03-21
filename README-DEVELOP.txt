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

