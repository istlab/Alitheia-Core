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
3.2

