#include "corbahandler.h"

#include <coss/CosNaming.h>

#include <boost/thread.hpp>
#include <boost/bind.hpp>

class OrbThread
{
public:
    OrbThread( CORBA::ORB* orb )
        : thread( boost::bind( &CORBA::ORB::run, orb ) )
    {
    }

private:
    boost::thread thread;
};

CorbaHandler::CorbaHandler()
{
    char* params[] = { "", "-ORBDefaultInitRef", "corbaloc::localhost:900" };
    int count = 3;
    orb = CORBA::ORB_init( count, params );
    orb_thread = new OrbThread( orb );
}

CorbaHandler::~CorbaHandler()
{
    delete orb_thread;
}

CorbaHandler* CorbaHandler::instance()
{
    static CorbaHandler handler;
    return &handler;
}

CORBA::Object_var CorbaHandler::getObject( const char* name ) const throw (CORBA::Exception)
{
    const CORBA::Object_var nsobj = orb->resolve_initial_references( "NameService" );
    const CosNaming::NamingContext_var nc = CosNaming::NamingContext::_narrow( nsobj );

    CosNaming::Name cosName;
    cosName.length( 1 );
    cosName[ 0 ].id = CORBA::string_dup( name );
    cosName[ 0 ].kind = CORBA::string_dup( "" );

    return nc->resolve( cosName );
}

void CorbaHandler::exportObject( CORBA::Object_ptr obj, const char* name )
{
    const CORBA::Object_var nsobj = orb->resolve_initial_references( "NameService" );
    const CosNaming::NamingContext_var nc = CosNaming::NamingContext::_narrow( nsobj );

    CosNaming::Name cosName;
    cosName.length( 1 );
    cosName[ 0 ].id = CORBA::string_dup( name );
    cosName[ 0 ].kind = CORBA::string_dup( "" );

    nc->rebind( cosName, obj );
}
