#include <CORBA.h>
#include <coss/CosNaming.h>

#include "Alitheia.h"

#include <fstream> 

using namespace std;
using namespace alitheia;

int main( int argc, char **argv)
{
    CORBA::ORB_var orb = CORBA::ORB_init( argc, argv);

    int rc = 0;
    /*if (argc != 2) {
        cerr << "usage: " << argv[0] << " message\n";
        exit(1);
    }*/
    
    try {
        CORBA::Object_var nsobj = orb->resolve_initial_references("NameService");
        CosNaming::NamingContext_var nc = CosNaming::NamingContext::_narrow( nsobj );

        CosNaming::Name name;
        name.length( 1 );
        name[ 0 ].id = CORBA::string_dup( "Logger" );
        name[ 0 ].kind = CORBA::string_dup( "" );

        CORBA::Object_var obj = nc->resolve( name );

        Logger_var f = Logger::_narrow( obj );
        f->info( argv[1] );
    }
    catch(CORBA::ORB::InvalidName_catch& ex)
    {
        ex->_print(cerr);
        cerr << endl;
        cerr << "possible cause: can't locate Naming Service\n";
        rc = 1;
    }
    catch(CORBA::SystemException_catch& ex)
    {
        ex -> _print(cerr);
        cerr << endl;
        rc = 1;
    }

    return rc;
}
