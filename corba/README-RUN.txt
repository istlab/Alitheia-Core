***
*** README-RUN
***

This README explains how to tun the CORBA services in an Alitheia
system.

***
*** Enable the services
***

Hence the CORBA services are an option part within the Alitheia
system, you need to enable it before you can use it.

To enable the corbaservice running within the Alitheia system, edit
Equinox' config.ini and set the following value:

eu.sqooss.corba.enable=true

To set the orb configuration including the nameserver the CORBA
service is using, set the value

eu.sqooss.corba.orb.nameservice=<...>

It's default value is

"corbaloc:iiop:1.2@localhost:2809/NameService"

This means, that you need to have a running ordb (included in the
j2sdk) on port 2809.
Use the following command to start it:

orbd -ORBInitialPort 2809

