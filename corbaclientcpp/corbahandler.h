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

private:
    OrbThread* orb_thread;
    CORBA::ORB_var orb;
};

#endif
