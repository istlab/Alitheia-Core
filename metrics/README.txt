Creating a new plug-in
-----------------------

1. Use the provided maven archetype

mvn archetype:create  
    -DgroupId=your.package
    -DartifactId=pluginname
    -DarchetypeGroupId=eu.sqooss.metrics 
    -DarchetypeArtifactId=archetype 
    -DarchetypeVersion=1.0

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

    If your bundle must include an external library, then you 
    should read the documentation of the maven-bundle-plugin on how
    to do it. You can find it here:
    http://felix.apache.org/site/apache-felix-maven-bundle-plugin-bnd.html

4. That's hopefully it.
