#include "database.h"

#include "dbobject.h"
#include "corbahandler.h"

#include <exception>

namespace Alitheia
{
    class Database::Private
    {
    public:
        Private( Database* q )
            : q( q )
        {
        }
        
    private:
        Database* const q;

    public:
        eu::sqooss::impl::service::corba::alitheia::Database_var database;
    };
}

using namespace Alitheia;
using namespace eu::sqooss::impl::service::corba;
using std::cerr;
using std::endl;
using std::exception;

Database::Database()
    : d( new Private( this ) )
{
    try
    {
        d->database = alitheia::Database::_narrow( CorbaHandler::instance()->getObject( "Database" ) );
    }
    catch( ... )
    {
         cerr << "Got an exception while getting an instance of the Database. Make sure the Alitheia system is running and eu.sqooss.service.corbaservice is loaded." << endl;
        throw exception();
    }
}

Database::~Database()
{
    delete d;
}

bool Database::addRecord( const DAObject& object )
{
    return d->database->addRecord( object );
}
