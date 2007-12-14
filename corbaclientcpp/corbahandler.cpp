#include "corbahandler.h"

#include <coss/CosNaming.h>

CorbaHandler::CorbaHandler()
{
    char* params[] = { "", "-ORBDefaultInitRef", "corbaloc::localhost:1050" };
    int count = 3;
    orb = CORBA::ORB_init( count, params);
}

CorbaHandler::~CorbaHandler()
{
}

CorbaHandler* CorbaHandler::instance()
{
    static CorbaHandler handler;
    return &handler;
}

CORBA::Object_var CorbaHandler::getObject( const char* className ) const //throw CORBA::Exception()
{
    const CORBA::Object_var nsobj = orb->resolve_initial_references( "NameService" );
    const CosNaming::NamingContext_var nc = CosNaming::NamingContext::_narrow( nsobj );

    CosNaming::Name name;
    name.length( 1 );
    name[ 0 ].id = CORBA::string_dup( className );
    name[ 0 ].kind = CORBA::string_dup( "" );

    return nc->resolve( name );
}
