Creating a new plug-in
----------------------

1. Use the provided maven archetype

mvn archetype:generate 
	-DgroupId=your.package
	-DartifactId=pluginame
	-Dversion=xxx  (replace xxx with your plug-in's version) 
	-Dpackage=your.package.pluginname
	-DarchetypeGroupId=eu.sqooss.plugins 
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
3.2 Change the <Bundle*> tags to meaningful values.  
3.3 In the dependencies section, add the maven dependencies for any external 
libraries that you use, including their dependencies. Plug-ins should be 
self-standing. In case you need an external Maven repository, include it in the
appropriate section inside the plug-ins pom.

3.4 Embed-Dependency and Import-Package control which external dependencies get
embeded into the resulting plug-in jar. By default, all external dependencies
marked with <scope> equal to 'compile' or 'runtime' will be included verbatim.
Import-Package controls which packages exported by other bundles will be
included.  This can be a shared library (present in the external/ subdir).
Normaly, you should not need to modify the default value. For more info about
both tags see the bnd tool documentation: http://www.aqute.biz/Code/Bnd

4.That's it! You should now have a dummy implementation of an SCMAccessor and the
corresponding updater
