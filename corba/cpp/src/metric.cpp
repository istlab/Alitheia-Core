#include "metric.h"

#include "core.h"
#include "dbobject.h"

#include <CORBA.h>

#include <algorithm>
#include <iterator>
#include <sstream>

#include "boost/encoding/base64.hpp"
#include "boost/encoding/encoding.hpp"

using namespace boost::encoding;

using namespace Alitheia;
using namespace eu::sqooss::impl::service::corba;

using std::string;
using std::stringstream;
using std::vector;

namespace Alitheia
{
    /*
     * \internal
     */
    class AbstractMetric::Private
    {
    public:
        Private( AbstractMetric *q )
            : q( q ),
              id( -1 )
        {
        }

    private:
        AbstractMetric* const q;

    public:
        string name;
        int id;
    };
}

/**
 * Creates a new AbstractMetric.
 */
AbstractMetric::AbstractMetric()
    : d( new Private( this ) )
{
}

/**
 * Destroys the AbstractMetric.
 * The metric is automatically unregistered from
 * the core, if it was registered before.
 */
AbstractMetric::~AbstractMetric()
{
    if( d->id != -1 )
        Core::instance()->unregisterMetric( this );
    delete d;
}

/**
 * Marshaller method for install()
 * Called via the CORBA ORB.
 */
CORBA::Boolean AbstractMetric::doInstall()
{
    return install();
}

/**
 * Marshaller method for remove()
 * Called via the CORBA ORB.
 */
CORBA::Boolean AbstractMetric::doRemove()
{
    return remove();
}

/**
 * Marshaller method for update()
 * Called via the CORBA ORB.
 */
CORBA::Boolean AbstractMetric::doUpdate()
{
    return update();
}

/**
 * Register the metric to the DB. Subclasses can run their custom
 * initialization routines.
 */
bool AbstractMetric::install()
{
    return false;
}

/**
 * Remove a plug-in's record from the DB. The DB's referential integrity
 * mechanisms are expected to automatically remove associated records.
 * Subclasses should also clean up any custom tables created.
 */
bool AbstractMetric::remove()
{
    return false;
}

/**
  * After installing a new version of the metric, try to  
  * update the results. The metric may opt to partially
  * or fully update its results tables or files.
  */
bool AbstractMetric::update()
{
    return false;
}

/**
  * Retrieve the installation date for this plug-in version
  * The default implementation reads the date from the database.
  */
string AbstractMetric::dateInstalled() const
{
    vector<Metric> metrics = getSupportedMetrics();
    if( metrics.empty() )
        return string();

    return metrics.front().plugin.installdate;
}

/**
 * Marshaller method for author()
 * Called via the CORBA ORB.
 */
char* AbstractMetric::getAuthor()
{
    return CORBA::string_dup( author().c_str() );
}

/**
 * Marshaller method for author()
 * Called via the CORBA ORB.
 */
char* AbstractMetric::getDescription()
{
    return CORBA::string_dup( description().c_str() );
}

/**
 * Marshaller method for author()
 * Called via the CORBA ORB.
 */
char* AbstractMetric::getName()
{
    return CORBA::string_dup( name().c_str() );
}

/**
 * Marshaller method for author()
 * Called via the CORBA ORB.
 */
char* AbstractMetric::getVersion()
{
    return CORBA::string_dup( version().c_str() );
}

/**
 * Marshaller method for author()
 * Called via the CORBA ORB.
 */
char* AbstractMetric::getDateInstalled()
{
    return CORBA::string_dup( dateInstalled().c_str() );
}

/**
 * Returns the metric's name within the ORB.
 */
const string& AbstractMetric::orbName() const
{
    return d->name;
}

/**
 * Set's the name of this metric within the ORB.
 * Called by the Core upon registration.
 */
void AbstractMetric::setOrbName( const string& orbName )
{
    d->name = orbName;
}

/**
 * Returns the metric's internal id.
 */
int AbstractMetric::id() const
{
    return d->id;
}

/**
  * Sets the metric's internal id.
  * Called by the Core upon registration.
  */
void AbstractMetric::setId( int id )
{
    d->id = id;
}

/**
 * Get the description objects for all metrics supported by this plug-in
 * as found in the database.
 */
vector<Metric> AbstractMetric::getSupportedMetrics() const
{
    return Core::instance()->getSupportedMetrics( this );
}

/**
  * Add a supported metric description to the database.
  *
  * @param description String description of the metric
  * @param mnemonic A short mnemonic description
  * @param type The metric type of the supported metric
  * @return True if the operation succeeds, false otherwise (i.e. duplicates etc)
  */
bool AbstractMetric::addSupportedMetrics( const string& description, const string& mnemonic, MetricType::Type type )
{
    return Core::instance()->addSupportedMetrics( this, description, mnemonic, type );
}

const std::string ResultEntry::MimeTypeTypeInteger = "type/integer";
const std::string ResultEntry::MimeTypeTypeLong    = "type/long";
const std::string ResultEntry::MimeTypeTypeFloat   = "type/float";
const std::string ResultEntry::MimeTypeTypeDouble  = "type/double";
const std::string ResultEntry::MimeTypeTextPlain   = "text/plain";
const std::string ResultEntry::MimeTypeTextHtml    = "text/html";
const std::string ResultEntry::MimeTypeTextCsv     = "text/csv";
const std::string ResultEntry::MimeTypeImageGif    = "image/gif";
const std::string ResultEntry::MimeTypeImagePng    = "image/png";
const std::string ResultEntry::MimeTypeImageJpeg   = "image/jpeg";

ResultEntry::ResultEntry( const value_type& value, const std::string& mimeType, const std::string& mnemonic )
    : value( value ),
      mimeType( mimeType ),
      mnemonic( mnemonic )
{
    // even accept std::string for those, but convert them to vector<char>
    if( value.type() == typeid( string ) && ( mimeType == MimeTypeImageGif || 
                                              mimeType == MimeTypeImagePng || 
                                              mimeType == MimeTypeImageJpeg ) )
    {
        const string s = boost::get< string >( value );
        vector< char > binary( s.length() );
        std::copy( s.begin(), s.end(), std::back_inserter( binary ) );
        this->value = binary;
    }
}

ResultEntry::ResultEntry( const eu::sqooss::impl::service::corba::alitheia::ResultEntry& entry )
    : mimeType( entry.mimeType ),
      mnemonic( entry.mnemonic )
{
    stringstream ss;
    ss << entry.value;
    
    if( mimeType == MimeTypeTypeInteger )
    {
        int v;
        ss >> v;
        value = v;
    }
    else if( mimeType == MimeTypeTypeLong )
    {
        long v;
        ss >> v;
        value = v;
    }
    else if( mimeType == MimeTypeTypeFloat )
    {
        float v;
        ss >> v;
        value = v;
    }
    else if( mimeType == MimeTypeTypeDouble )
    {
        double v;
        ss >> v;
        value = v;
    }
    else if( mimeType == MimeTypeTextPlain || mimeType == MimeTypeTextHtml  )
    {
        value = ss.str();
    }
    else
    {
        const string base64data = ss.str();
        vector< char > binary( base64data.length() * 3 / 4 );
        decode( base64data.begin(), base64data.end(), std::back_inserter( binary ), base64() );
        value = binary;
    }
}

ResultEntry::~ResultEntry()
{
}

alitheia::ResultEntry ResultEntry::toCorba() const
{
    alitheia::ResultEntry result;
     
    stringstream ss;

    if( value.type() == typeid( int ) )
        ss << boost::get< int >( value );
    else if( value.type() == typeid( long ) )
        ss << boost::get< long >( value );
    else if( value.type() == typeid( float ) )
        ss << boost::get< float >( value );
    else if( value.type() == typeid( double ) )
        ss << boost::get< double >( value );
    else if( value.type() == typeid( string ) )
        ss << boost::get< string >( value );
    else if( value.type() == typeid( vector< char > ) )
    {
        const vector< char > binary =  boost::get< vector< char > >( value );
        string base64data;
        encode( binary.begin(), binary.end(), std::back_inserter( base64data ), base64() );
        ss << base64data;
    }
        
    result.value = CORBA::string_dup( ss.str().c_str() );
    
    result.mimeType = CORBA::string_dup( mimeType.c_str() );
    result.mnemonic = CORBA::string_dup( mnemonic.c_str() );
    
    return result;
}

/**
 * Marshaller method for getResult()
 * Called via the CORBA ORB.
 */
alitheia::Result* ProjectVersionMetric::doGetResult( const alitheia::ProjectVersion& projectVersion,
                                                     const alitheia::Metric& m )
{
    alitheia::Result* result = new alitheia::Result();
    vector< ResultEntry > entries = getResult( ProjectVersion( projectVersion ), Metric( m ) );
    for( uint i = 0; i < entries.size(); ++i )
        (*result)[ i ] = entries[ i ].toCorba();
    
    return result;
}

/**
 * Marshaller method for run()
 * Called via the CORBA ORB.
 */
void ProjectVersionMetric::doRun( const alitheia::ProjectVersion& projectVersion )
{
    ProjectVersion version( projectVersion );
    run( version );
}

/**
 * Marshaller method for getResult()
 * Called via the CORBA ORB.
 */
alitheia::Result* ProjectFileMetric::doGetResult( const alitheia::ProjectFile& projectFile,
                                                  const alitheia::Metric& m )
{
    const vector< ResultEntry > entries = getResult( ProjectFile( projectFile ), Metric( m ) );
    alitheia::Result* const result = new alitheia::Result();
    result->length( entries.size() );

    for( size_t i = 0; i < entries.size(); ++i )
        result->operator[]( i ) = entries[ i ].toCorba();
    
    return result;
}

/**
 * Marshaller method for run()
 * Called via the CORBA ORB.
 */
void ProjectFileMetric::doRun( const alitheia::ProjectFile& projectFile )
{
    ProjectFile file( projectFile );
    run( file );
}

/**
 * Marshaller method for getResult()
 * Called via the CORBA ORB.
 */
alitheia::Result* StoredProjectMetric::doGetResult( const alitheia::StoredProject& storedProject,
                                                    const alitheia::Metric& m )
{
    alitheia::Result* result = new alitheia::Result();
    vector< ResultEntry > entries = getResult( StoredProject( storedProject ), Metric( m ) );
    for( uint i = 0; i < entries.size(); ++i )
        (*result)[ i ] = entries[ i ].toCorba();
     return result;
}

/**
 * Marshaller method for run()
 * Called via the CORBA ORB.
 */
void StoredProjectMetric::doRun( const alitheia::StoredProject& storedProject )
{
    StoredProject project( storedProject );
    run( project );
}

/**
 * Marshaller method for getResult()
 * Called via the CORBA ORB.
 */
alitheia::Result* FileGroupMetric::doGetResult( const alitheia::FileGroup& fileGroup,
                                                const alitheia::Metric& m )
{
    alitheia::Result* result = new alitheia::Result();
    vector< ResultEntry > entries = getResult( FileGroup( fileGroup ), Metric( m ) );
    for( uint i = 0; i < entries.size(); ++i )
        (*result)[ i ] = entries[ i ].toCorba();
     return result;
}

/**
 * Marshaller method for run()
 * Called via the CORBA ORB.
 */
void FileGroupMetric::doRun( const alitheia::FileGroup& fileGroup )
{
    FileGroup group( fileGroup );
    run( group );
}
