Creating a new metric 
---------------------

1. Use the provided maven archetype (see below how).  Before running it, do
mvn install at the top level directory. Also, make sure that artifactId
parameter contains the following string: ".metrics." in order to allow the
Alitheia Core runtime to pick up at runtime. Examples of
eligible package names are the following:

-gr.aueb.metrics.foo
-com.metrics.bar.foo
-eu.sqooss.metrics.baz

Then run the following:

mvn archetype:generate 
    -DgroupId=your.package
    -DartifactId=metricname
    -Dversion=0.x   (replace x with your metrics version)
    -Dpackage=your.package.pluginname 
    -DarchetypeGroupId=eu.sqooss.metrics
    -DarchetypeArtifactId=archetype 
    -DarchetypeVersion=1.0-SNAPSHOT

2. Add the plugin to the pom.xml file in the <modules> section, if the
archetype creator does not do that for your

  <modules>
    ...
    <module>pluginame</module>
    ...
  </modules>

3. Change to the pluginame directory and edit the pom.xml file
3.1 Change the <name> tag to something meaningful
3.2 Change the <version> tag to something meaningful
3.3 In the  <instuctions>....</instructions> section of the file,
    change the included tags to match your plug-in's details.
    Important is to set the Bundle-Activator parameter to your
    bundle's activator class, otherwise the plug-in will fail to
    start. 

3.4 If your bundle must include an external library, then you 
    should read the documentation of the maven-bundle-plugin on how
    to do it. You can find it here:
    http://felix.apache.org/site/apache-felix-maven-bundle-plugin-bnd.html
   
3.5 If your plug-in uses a custom table to store results.

    Hibernate mapping files are called *.hbm.xml and by convention live
    next to the Java files they belong to; we have the additional
    convention that the private database table sources (Java and
    Hibernate) live in a package named db, so we get

    src/eu/sqooss/metrics/<foo>/db 

    To include Hibernate mapping files in the generated bundle,
    include the following extract in the <plugins>....</plugins>
    section of the generated pom.xml file.

    <resources>
      <resource>
        <targetPath>eu/sqooss/service/db</targetPath>
        <directory>src/main/java/${groupId}/${artifactId}/db</directory>
        <includes>
          <include>**/*.xml</include>
        </includes>
      </resource>
      <resource>
        <directory>src/main/resources</directory>
        <includes>
          <include>**/*</include>
        </includes>
      </resource>
    </resources>

    Also, automatic import package resolution step might not work well. 
    You should add the following line to the <instuctions> section

    <Import-Package>*, eu.sqooss.metrics.db.*</Import-Package>

4. That's hopefully it.

