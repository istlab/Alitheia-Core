#ifndef CORBAHANDLER_H
#define CORBAHANDLER_H

#include <CORBA.h>

class OrbThread;

class CorbaHandler
{
protected:
    CorbaHandler();

private:
    CorbaHandler( const CorbaHandler& ) {}
    void operator=( const CorbaHandler& );

public:
    virtual ~CorbaHandler();

    static CorbaHandler* instance();

    CORBA::Object_var getObject( const char* name ) const throw (CORBA::Exception);
    void exportObject( CORBA::Object_ptr obj, const char* name );
    void unexportObject( const char* name );

    void run();
    void shutdown();

private:
    OrbThread* orb_thread;
    CORBA::ORB_var orb;
    CORBA::Object_var poaobj;
    PortableServer::POA_var poa;
    PortableServer::POAManager_var mgr;
};

#endif
