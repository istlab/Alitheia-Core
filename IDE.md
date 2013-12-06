## Working with IDEs

The following instructions have been tested and confirmed working 
with Eclipse Kepler (4.3) and IntelliJ 12.1 on MacOSX.

### Eclipse
1. Checkout the project from its repository, for example
    
    `git clone git@github.com:istlab/Alitheia-Core.git`

2. Do the following 
  ```bash
    mvn clean install
    mvn -DdownloadJavadoc -Ddownloadsources eclipse:eclipse
  ```

3. In Eclipse, choose `Import->General->Existing` Projects into Workspace.
In the next dialogue,
you enter the checkout directory and a list of project modules appears. Here
you should select only the modules you will be working with.

4. To debug, you run the `debug.sh` script from the top-level directory. This
will start Alitheia Core in debug mode; initialisation will stop until an
external debug is attached. To attach the eclipse debugger, click on the
`Run menu->Debug configurations`, then select the `Remote Java Application` from
the list and click on the new launch configuration button. Add a name click
debug. Eclipse will connect to the waiting virtual machine and will stop
at any breakpoint you have set in the code.

### IntelliJ

1. Checkout the project from its repository, for example

    `git clone git@github.com:istlab/Alitheia-Core.git`

2. Do the following 

  ```bash
    mvn clean install
    mvn -DdownloadJavadoc -Ddownloadsources intellij:intellij
  ```
    
3. In IntelliJ, go to `File->Open` project and choose the Alitheia Core top-level 
directory. Importing will start.

4. To debug, create a debug configuration (Run->Debug->Edit Configurations) and
then click on + and select Remote. Set port to 8000 and click Debug. Then run
the `debug.sh` script on the top-level directory.

