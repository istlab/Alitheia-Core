#include "database.h"

#include "dbobject.h"
#include "corbahandler.h"

#include <exception>

#include <boost/variant/get.hpp>

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
using std::vector;

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

bool Database::addCorbaRecord( CORBA::Any& record )
{
    return d->database->addRecord( record );
}

bool Database::updateCorbaRecord( CORBA::Any& record )
{
    return d->database->updateRecord( record );
}

bool Database::deleteRecord( const DAObject& object )
{
    return d->database->deleteRecord( object );
}

CORBA::Any* Database::findObjectById( const CORBA::Any& type, int id )
{
    return d->database->findObjectById( type, id );
}

CORBA::Any variant_to_any( const boost::variant< int, std::string >& variant )
{
    CORBA::Any result;
    if( variant.type() == typeid( int ) )
        result <<= CORBA::Long( boost::get< int >( variant ) );
    if( variant.type() == typeid( std::string ) )
        result <<= CORBA::string_dup( boost::get< std::string >( variant ).c_str() );
    return result;
}

alitheia::map_entry pair_to_corba_map_entry( const std::pair< Database::property_map_key, Database::property_map_value >& p )
{
    alitheia::map_entry result;
    result.key = CORBA::string_dup( p.first.c_str() );
    result.value = variant_to_any( p.second );
    return result;
}

alitheia::map property_map_to_corba_map( const Database::property_map& properties )
{
    alitheia::map result;
    result.length( properties.size() );

    size_t idx = 0;
    for( Database::property_map::const_iterator it = properties.begin(); it != properties.end(); ++it )
        result[ idx++ ] = pair_to_corba_map_entry( *it );
    
    return result;
}

vector< CORBA::Any > Database::findObjectsByProperties( const CORBA::Any& type, const property_map& properties )
{
    const alitheia::list& objects = *(d->database->findObjectsByProperties( type, property_map_to_corba_map( properties ) ) );

    const uint length = objects.length();
    vector< CORBA::Any > result;
    for( uint i = 0; i < length; ++i )
        result.push_back( objects[ i ] );

    return result;
}
