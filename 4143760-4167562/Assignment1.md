# IN4189 Software Reengineering - Reverse Engineering and Detection Report 
By Martijn den Hoedt - 4143760, Anton Bouter - 4167562

## Introduction
[Alitheia Core](http://www.sqo-oss.org/about) is a platform for automatic evaluation of software that is designed to simplify software engineering research. It can produce a broad picture of the quality of an Open Source project by analysing both hard project artifacts (source code) and soft project artifacts (mailing list, bug tracker entries).
We performed a software analysis on the Alitheia Core to find shortcoming such as design flaws and code smells. The first chapter of this report will describe the system and its structure, while the second chapter is dedicated to pinpointing shortcomings of the Alitheia Core. The focus is on detecting violations of the S.O.L.I.D. object-oriented design principles. These include the following five principles:

 * Single responsibility principle
 * Open/closed principle
 * Liskov substitution principle
 * Interface segregation principle
 * Dependency inversion principle
 
To simplify the detection procedure, the following software evaluation tools have been used: SonarQube, inCode, ...

## Initial Understanding and Detailed Model Capture

### Main features
What makes Alitheia Core stand out is that it not only evaluates the final product, but also the developing process. This is done by evaluating the so called soft project artifacts, such as mailing lists and bug tracker entries, which leads to a broader picture of the quality of the software project. The different aspects of that are analysed are aggregated through a [multilevel quality model](http://www.dmst.aueb.gr/dds/pubs/conf/2008-OSS-qmodel/html/SGSS08.htm).

The Alitheia Core can also be used to automate and parallellize the execution of custom experiments. Studies on empirical data can easily be performed and the Alitheia Core can also be extended to produce any measurement you are interested in.

### Important source code entities

The main class of the Alitheia Core is AlitheiaCore.java, in the eu.sqooss.core package, which initializes the core components of the system and provides a static instance of AlitheiaCore. All services of the AlitheiaCore classes can then be called by using the methods defined in the AlitheiaCoreService interface, which is present in the same package as the AlitheiaCore class. These services are implemented in the eu.sqooss.service package and divided into a large number of packages, such as admin, db, scheduler, updater and util. Each package can also contain classes that the service depends on.

### Impression of design and implementation

We think that the Alitheia Core system looks well structured, because the classes seem to be properly distributed among packages with names that provide useful information. The core is properly isolated from the classes that provide services. Many classes also contain javadoc comments and additional comments to clarify functions when needed. However, the number of tests is very limited. We therefore have a relatively good first impression of the design and implementation of the system. Although the test suite should be expanded, we think reengineering the Alitheia Core is feasible, because the javadoc documentation makes it much easier.

### Exceptional entities

To study the exceptional entities, such as packages, classes and methods, a UML diagram can be very useful. Such a diagram shows which classes are related and in what way. A UML diagram can also be reverse engineered by a tool such as [UMLGraph](http://www.umlgraph.org/). The diagram in figure 1 displays all database objects and was created by Georgios Gousios using the UMLGraph tool on the Alitheia Core system [[1]](http://www.umlgraph.org/doc/ceg-er.html). From this diagram we can clearly see that many classes depend on the StoredProject class. The same goes for the Metric, ProjectVersion and Bug classes. All of these classes are part of the eu.sqooss.service.db package, which definitely is an exceptional entity, because it contains a very large number of classes. 

<img src="./img/uml.png" width="931" height="437px" />

<center>*Figure 1: UML Diagram of Alitheia Core*</center>

### Inheritance structure

As described earlier, the system is composed of many service interfaces that extend the AlitheiaCoreService interface, such as DBService, AdminService, LogManager and RestService. The implementations of all these classes must also implement the basic functions of the AlitheiaCoreService class.

### Step through execution



Finally, collisions are handled by locking the database whenever a thread starts a database session, such that no other threads can commit changes to the database. When changes are committed, the current session is closed and the lock on the database is released. The changes made during the current session can also be reverted by the function rollbackDBSession in DBService.  
