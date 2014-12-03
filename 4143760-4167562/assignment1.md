# IN4189 Software Reengineering - Reverse Engineering and Detection Report 
By Martijn den Hoedt - 4143760, Anton Bouter - 4167562

## Introduction
[Alitheia Core](http://www.sqo-oss.org/) is a platform for automatic evaluation of software that is designed to simplify software engineering research. It can produce a broad picture of the quality of an Open Source project by analysing both hard project artifacts (source code) and soft project artifacts (mailing list, bug tracker entries).
We performed a software analysis on the Alitheia Core to find shortcoming such as design flaws and code smells. The first chapter of this report will describe the system and its structure, while the second chapter is dedicated to pinpointing shortcomings of the Alitheia Core. The focus is on detecting violations of the S.O.L.I.D. object-oriented design principles. These include the following five principles:

 * Single responsibility principle
 * Open/closed principle
 * Liskov substitution principle
 * Interface segregation principle
 * Dependency inversion principle

To simplify the detection procedure, the following software evaluation tools have been used: 

 * [SonarQube](http://www.sonarqube.org/)
    - Open platform to manage code quality, which covers criteria such as complexity, potential bugs, duplications, architecture & design.
 * [inCode](https://www.intooitus.com/products/incode)
    - Detects design flaws automatically and helps you resolve them.
 * [X-Ray](http://xray.inf.usi.ch/xray.php)
    - Software visualization tool that provides e.g. a system complexity form and a class and package dependency view.
 * [UMLGraph](http://www.umlgraph.org/)
    - Allows automatic drawing of UML class and sequence diagrams

## Initial Understanding and Detailed Model Capture

To find certain shortcomings in the design of the system, we must first understand it. This chapter is dedicated to the process of understanding the model and displaying its properties clearly by using reverse engineering tools to create models.

### Main features
What makes Alitheia Core stand out is that it not only evaluates the final product, but also the developing process. This is done by evaluating the so called soft project artifacts, such as mailing lists and bug tracker entries, which leads to a broader picture of the quality of the software project. The different aspects of that are analysed are aggregated through a [multilevel quality model](http://www.dmst.aueb.gr/dds/pubs/conf/2008-OSS-qmodel/html/SGSS08.htm) [[1]](http://www.sqo-oss.org/about).

The Alitheia Core can also be used to automate and parallellize the execution of custom experiments. Studies on empirical data can easily be performed and the Alitheia Core can also be extended to produce any measurement you are interested in.

### Important source code entities

The main class of the Alitheia Core is AlitheiaCore.java, in the eu.sqooss.core package, which initializes the core components of the system and provides a static instance of AlitheiaCore. All services of the AlitheiaCore classes can then be called by using the methods defined in the AlitheiaCoreService interface, which is present in the same package as the AlitheiaCore class. These services are implemented in the eu.sqooss.service package and divided into a large number of packages, such as admin, db, scheduler, updater and util. Each package can also contain classes that the service depends on.

### Impression of design and implementation

We think that the Alitheia Core system looks well structured, because the classes seem to be properly distributed among packages with names that provide useful information. The core is properly isolated from the classes that provide services. Many classes also contain javadoc comments and additional comments to clarify functions when needed. However, the number of tests is very limited. We therefore have a relatively good first impression of the design and implementation of the system. Although the test suite should be expanded, we think reengineering the Alitheia Core is feasible, because the javadoc documentation makes it much easier.

### Exceptional entities

To study the exceptional entities, such as packages, classes and methods, a UML diagram can be very useful. Such a diagram shows which classes are related and in what way. A UML diagram can also be reverse engineered by a tool such as [UMLGraph](http://www.umlgraph.org/). The diagram in figure 1 displays all database objects and was created by Georgios Gousios using the UMLGraph tool on the Alitheia Core system [[2]](http://www.umlgraph.org/doc/ceg-er.html). From this diagram we can clearly see that many classes depend on the StoredProject class. The same goes for the Metric, ProjectVersion and Bug classes. All of these classes are part of the eu.sqooss.service.db package, which definitely is an exceptional entity, because it contains a very large number of classes. 

<img src="./img/uml.png" width="931" height="437px" />

<center>*Figure 1: UML Diagram of the database entities of Alitheia Core*</center>

### Inheritance structure

As described earlier, the system is composed of many service interfaces that extend the AlitheiaCoreService interface, such as DBService, AdminService, LogManager and RestService. The implementations of all these classes must also implement the basic functions of the AlitheiaCoreService class.

### Step through execution

## S.O.L.I.D. Design Principles

The S.O.L.I.D. design principles were already named in the introduction, but to find shortcoming in the code, we shall explain them further in this section. 

### Interface Segregation Principle
According to this principle, no client should be forced to depend on methods it does not use. When the superclass of some class A contains methods that are not applicable to A, the Interface Segregation Principle is violated. When this happens, the superclass should be split such that its children do not have access to methods that are not applicable. 

## Problem Detection
[inCode](https://www.intooitus.com/products/incode) found nine God classes, two of them are scoring a 10 out of 10 (`GitUpdater` and `ContributionMetricImpl`). The `GitUpdater` 767 LOC, but another bigger class called `SVNUUpdaterImpl` has 993 LOC. 

### Single Responsibility Principle
This principle states that every software entity (class, function, variable, etc.) should have a single purpose and only one reason to change.  If it is violated, the violating entity should be split into two or more entities that satisfy the Single Responsibility Principle.

Both the `ContributionMetrixImpl` and the `GitUpdater` classes have many methods, use many attributes from many external classes. The `GitUpdater` class also contains multiple to-do's and code that is commented out. These two classes keep track of many things and can perform many actions, therefore they violate the single responsbility principle. Another example is the definition of `DecreasingLongComparator` and `RandomizedCompatator` within `MetricActivatorImpl.java`. In this way it's not easy to know such classes actually exists, not to mention reusing the code. 

### Open/Closed Principle (OCP) or Liskov Substitution Principle (LSP)

According to the Open/Closed principle, software entities should be open for extension, but closed for modification. This means that the software must be extensible without modifications to its source code. Also, an extension may not lead to a change in behaviour of the code that is extended.

The principles of object oriented programming are underlined by the Liskov Substitution principle. It states that subtypes must be substitutable for their base types. Therefore, if a method expects some object A as a parameter, this method should also accept any class that is a superclass of A. The post-conditions of the method should still hold after such a substitution.

### Dependency Inversion Principle (DIP)

This principle states that high-level modules should not depend on low-level modules. Additionally, abstractions should not depend on details, but details should depend on abstractions. This means that the high-level modules cannot simply use low-level modules to perform some task. An interface should be used that implements the functions of the low-level module. The high-level class then calls the methods of the interface and the low-level class implements the interface.

### Acyclic Dependencies Principle (ADP)

### Don't Repeat Yourself (DRY)
We have looked for duplicated code with [SonarQube](http://www.sonarqube.org/). This tool found 313 lines of code (LOC) in a total of 31,760 LOC. Thus only 1% of the Alitheia Core project is duplicate code. The project contains 15,688 LOC with Java, the rest is comments or whitespace. We investigated a few instances of code duplication. One of them is `BugResolution` (lines 121-135) and `BTSEntry` (lines 203-216). 

### Simple shortcomings
SonarQube is also able to detect a lot of other issues. In Alitheia Core are 1770 issues. These issues can point out confusing code formatting, like inconsistently placing curly braces. Also more important issues like never throwing generic exceptions, such as `Exception`, are detected in the Alitheia Core project.

SonarQube is also able to calculate the complexicity of the code. The [complexity metrics](http://docs.codehaus.org/display/SONAR/Metrics+-+Complexity) used by SonarQube count every  `if`, `for`, `while`, `case`, `catch`, `throw` and `return` statement. The `PluginsView` has the highest complexity per function, but also a high amount of code per method. The inCode tool also found classes and methods with a high complexity.
