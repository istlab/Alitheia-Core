#include "metric.h"

#include "core.h"
#include "dbobject.h"

#include <CORBA.h>

using namespace Alitheia;
using namespace eu::sqooss::impl::service::corba;
using std::string;
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

/**
 * Marshaller method for getResult()
 * Called via the CORBA ORB.
 */
char* ProjectVersionMetric::doGetResult( const alitheia::ProjectVersion& projectVersion )
{
    return CORBA::string_dup( getResult( ProjectVersion( projectVersion ) ).c_str() );
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
char* ProjectFileMetric::doGetResult( const alitheia::ProjectFile& projectFile )
{
    ProjectFile file( projectFile );
    return CORBA::string_dup( getResult( file ).c_str() );
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
char* StoredProjectMetric::doGetResult( const alitheia::StoredProject& storedProject )
{
    return CORBA::string_dup( getResult( StoredProject( storedProject ) ).c_str() );
}

/**
 * Marshaller method for run()
 * Called via the CORBA ORB.
 */
void StoredProjectMetric::doRun( const alitheia::StoredProject& storedProject )
{
//    return CORBA::string_dup( getResult( StoredProject( storedProject ) ).c_str() );
}

/**
 * Marshaller method for getResult()
 * Called via the CORBA ORB.
 */
char* FileGroupMetric::doGetResult( const alitheia::FileGroup& fileGroup )
{
    return CORBA::string_dup( getResult( FileGroup( fileGroup ) ).c_str() );
}

/**
 * Marshaller method for run()
 * Called via the CORBA ORB.
 */
void FileGroupMetric::doRun( const alitheia::FileGroup& fileGroup )
{
//    return CORBA::string_dup( getResult( FileGroup( fileGroup ) ).c_str() );
}
