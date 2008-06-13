#include "database.h"

#include "dbobject.h"
#include "corbahandler.h"

#include <exception>

#include <boost/variant/get.hpp>

namespace Alitheia
{
    /**
     * \internal
     */
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

/**
 * Constructs a new Database.
 */
Database::Database()
    : d( new Private( this ) )
{
    try
    {
        d->database = alitheia::Database::_narrow( CorbaHandler::instance()->getObject( "AlitheiaDatabase" ) );
    }
    catch( ... )
    {
         cerr << "Got an exception while getting an instance of the Database. Make sure the Alitheia system is running and eu.sqooss.service.corbaservice is loaded." << endl;
        throw exception();
    }
}

/**
 * Detroys this Database.
 */
Database::~Database()
{
    delete d;
}

/**
 * \internal
 * Adds \a record to the database.
 */
bool Database::addCorbaRecord( CORBA::Any& record )
{
    return d->database->addRecord( record );
}

/**
 * \internal
 * Updates \a record in the database.
 */
bool Database::updateCorbaRecord( CORBA::Any& record )
{
    return d->database->updateRecord( record );
}

/**
 * Removes \a object from the database.
 */
bool Database::deleteRecord( const DAObject& object )
{
    return d->database->deleteRecord( object );
}

/**
 * \intarnal
 * Finds an object having \a type by it's \a id.
 */
CORBA::Any* Database::findObjectById( const CORBA::Any& type, int id ) const
{
    return d->database->findObjectById( type, id );
}

/**
 * \internal
 * Creates property_map_value from an CORBA::Any \a any.
 */
Database::property_map_value any_to_variant( const CORBA::Any& any )
{
    Database::property_map_value result;
    const char* str;
    if( any >>= str )
        return result = std::string( str );

    CORBA::Boolean b;
    if ( any >>= CORBA::Any::to_boolean( b ) )
        return result = (bool)b;
    
    CORBA::Long l;
    if( any >>= l )
        return result = (int)l;
    
    alitheia::Developer dev;
    if( any >>= dev )
        return result = Developer::fromCorba( any );

    alitheia::Directory dir;
    if( any >>= dir )
        return result = Directory::fromCorba( any );

    alitheia::FileGroup group;
    if( any >>= group )
        return result = FileGroup::fromCorba( any );

    alitheia::Metric metric;
    if( any >>= metric )
        return result = Metric::fromCorba( any );

    alitheia::MetricType metricType;
    if( any >>= metricType )
        return result = MetricType::fromCorba( any );

    alitheia::Plugin plugin;
    if( any >>= plugin )
        return result = Plugin::fromCorba( any );

    alitheia::PluginConfiguration pluginConfiguration;
    if( any >>= pluginConfiguration )
        return result = PluginConfiguration::fromCorba( any );

    alitheia::ProjectFile projectFile;
    if( any >>= projectFile )
        return result = ProjectFile::fromCorba( any );

    alitheia::ProjectFileMeasurement projectFileMeasurement;
    if( any >>= projectFileMeasurement )
        return result = ProjectFileMeasurement::fromCorba( any );

    alitheia::ProjectVersion projectVersion;
    if( any >>= projectVersion )
        return result = ProjectVersion::fromCorba( any );

    alitheia::ProjectVersionMeasurement projectVersionMeasurement;
    if( any >>= projectVersionMeasurement )
        return result = ProjectVersionMeasurement::fromCorba( any );

    alitheia::StoredProject storedProject;
    if( any >>= storedProject )
        return result = StoredProject::fromCorba( any );

    return result;
}

/**
 * \internal
 * Creates a CORBA::Any from a property_map_value \a variant.
 */
CORBA::Any variant_to_any( const Database::property_map_value& variant )
{
    CORBA::Any result;
    if( variant.type() == typeid( int ) )
        result <<= CORBA::Long( boost::get< int >( variant ) );
    
    else if( variant.type() == typeid( std::string ) )
        result <<= CORBA::string_dup( boost::get< std::string >( variant ).c_str() );
    
    else if( variant.type() == typeid( Developer ) )
        result = boost::get< Developer >( variant );
    
    else if( variant.type() == typeid( Directory ) )
        result = boost::get< Directory >( variant );
    
    else if( variant.type() == typeid( FileGroup ) )
        result = boost::get< FileGroup >( variant );
    
    else if( variant.type() == typeid( Metric ) )
        result = boost::get< Metric >( variant );
    
    else if( variant.type() == typeid( MetricType ) )
        result = boost::get< MetricType >( variant );
    
    else if( variant.type() == typeid( PluginConfiguration ) )
        result = boost::get< PluginConfiguration >( variant );

    else if( variant.type() == typeid( PluginConfiguration ) )
        result = boost::get< PluginConfiguration >( variant );

    else if( variant.type() == typeid( ProjectFile ) )
        result = boost::get< ProjectFile >( variant );
    
    else if( variant.type() == typeid( ProjectFileMeasurement ) )
        result = boost::get< ProjectFileMeasurement >( variant );

    else if( variant.type() == typeid( ProjectVersion ) )
        result = boost::get< ProjectVersion >( variant );
    
    else if( variant.type() == typeid( ProjectVersionMeasurement ) )
        result = boost::get< ProjectVersionMeasurement >( variant );
    
    else if( variant.type() == typeid( StoredProject ) )
        result = boost::get< StoredProject >( variant );

    return result;
}

/**
 * \internal
 * Creates a map_entry from a key/value pair \a p.
 */
alitheia::map_entry pair_to_corba_map_entry( const std::pair< Database::property_map_key, Database::property_map_value >& p )
{
    alitheia::map_entry result;
    result.key = CORBA::string_dup( p.first.c_str() );
    result.value = variant_to_any( p.second );
    return result;
}

/**
 * \internal
 * Creates a map from a property_map \a properties.
 */
alitheia::map property_map_to_corba_map( const Database::property_map& properties )
{
    alitheia::map result;
    result.length( properties.size() );

    size_t idx = 0;
    for( Database::property_map::const_iterator it = properties.begin(); it != properties.end(); ++it )
        result[ idx++ ] = pair_to_corba_map_entry( *it );
    
    return result;
}

/**
 * \internal
 * Finds a list of objects of \a type by some \a properties.
 */
vector< CORBA::Any > Database::findObjectsByProperties( const CORBA::Any& type, const property_map& properties ) const
{
    const alitheia::list& objects = *(d->database->findObjectsByProperties( type, property_map_to_corba_map( properties ) ) );

    const uint length = objects.length();
    vector< CORBA::Any > result;
    for( uint i = 0; i < length; ++i )
        result.push_back( objects[ i ] );

    return result;
}
   
/**
 * Executes a Hibernate HQL query \a hql on the database.
 * \params is used for parameters.
 */
vector< Database::db_row_entry > Database::doHQL( const std::string& hql, const property_map& params )
{
    const alitheia::list& objects = *(d->database->doHQL( CORBA::string_dup( hql.c_str() ), 
                                                          property_map_to_corba_map( params ) ) );

    const uint length = objects.length();
    vector< db_row_entry > result;
    for( uint i = 0; i < length; ++i )
        result.push_back( any_to_variant( objects[ i ] ) );
    return result;
}

/**
 * Executes a SQL query \a hql on the database.
 * \params is used for parameters.
 */
vector< Database::db_row_entry > Database::doSQL( const std::string& sql, const property_map& params )
{
    const alitheia::list& objects = *(d->database->doSQL( CORBA::string_dup( sql.c_str() ), 
                                                          property_map_to_corba_map( params ) ) );

    const uint length = objects.length();
    vector< db_row_entry > result;
    for( uint i = 0; i < length; ++i )
        result.push_back( any_to_variant( objects[ i ] ) );
    return result;
}
