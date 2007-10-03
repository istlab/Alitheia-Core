This is the Maven project tree for the Alitheia system, its components and
metrics. The top-level tree is split into the following groups:

alitheia/
    Contains the source code and build definitions for the Alitheia core and
    its components.

metrics/
    Contain the source code of the various metric plug-ins developed for the
    Alitheia system.

ui/
    Contains the source code and build definition (will probably be Maven too)
    for the public-facing website (user interface), the CLI (admin interface)
    and the eclipse plugin (IDE interface). Also contains the SCL (SQO-OSS
    connector library) which is shared between the UIs.


extlibs/
    Contains a Maven style repository of all libraries required for building
    the Alitheia system. 
