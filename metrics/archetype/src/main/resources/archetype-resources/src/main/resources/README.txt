Here you put all the resources that are going to be used from your
plug-in. This includes all files except from source code and 
Hibernate mapping files (if your plug-in uses its own tables).
The files you put here will appear at the root of your module's
classpath, so you can use a Classloader to load a resource as
follows:

InputStream is = getClass().getClassLoader().getResourceAsStream('file.dat');