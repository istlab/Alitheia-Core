#include "corbahandler.h"

#include <coss/CosNaming.h>

#include <boost/thread.hpp>
#include <boost/bind.hpp>

#include <iostream>

using namespace std;

/**
 * \internal
 * OrbThread is the background thread running the ORB.
 */
class OrbThread
{
public:
    /**
     * Creates a new OrbThread running the ORB \a orb.
     */
    OrbThread( CORBA::ORB* orb )
        : thread( boost::bind( &CORBA::ORB::run, orb ) )
    {
    }

    /**
     * Waits for the thread to be done.
     */
    void join()
    {
        thread.join();
    }
    
private:
    boost::thread thread;
};

/**
 * Creates a new CorbaHandler.
 */
CorbaHandler::CorbaHandler()
{
    int argc = 3;
    char* argv[] = { "", "-ORBInitRef", "NameService=corbaloc:iiop:1.2@localhost:2809/NameService" };
 
    try{
        orb = CORBA::ORB_init( argc, argv );
        poaobj = orb->resolve_initial_references( "RootPOA" );
    
        poa = PortableServer::POA::_narrow( poaobj );
        mgr = poa->the_POAManager();
        mgr->activate();
    }
    catch( CORBA::SystemException_catch& ex )
    {
        ex->_print( cerr );
        cerr << "Got an exception while initializing the CorbaHandler. Make sure the ordb is running on port 2809." << endl;
    }
    orb_thread = new OrbThread( orb );
}

/**
 * Destroys the CorbaHandler.
 */
CorbaHandler::~CorbaHandler()
{
//    delete orb_thread;
}

/**
 * Waits for the ORB thread to finish.
 */
void CorbaHandler::run()
{
    orb_thread->join();
}

/**
 * Shuts down the ORB thread.
 */
void CorbaHandler::shutdown()
{
    orb->shutdown( TRUE );
}

/**
 * Returns a singleton instance of CorbaHandler.
 */
CorbaHandler* CorbaHandler::instance()
{
    static CorbaHandler handler;
    return &handler;
}

/**
 * Imports an object identified by \a name from the CORBA ORB.
 * @return A CORBA style object reference to the object.
 */
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

/**
 * Exports \a obj to the CORBA ORB using \a name as identifier.
 */
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

/**
 * Stops exporting an object identified by \a name into the ORB.
 */
void CorbaHandler::unexportObject( const char* name )
{
    const CORBA::Object_var nsobj = orb->resolve_initial_references( "NameService" );
    const CosNaming::NamingContext_var nc = CosNaming::NamingContext::_narrow( nsobj );

    CosNaming::Name cosName;
    cosName.length( 1 );
    cosName[ 0 ].id = CORBA::string_dup( name );
    cosName[ 0 ].kind = CORBA::string_dup( "" );

    nc->unbind( cosName );    
}
