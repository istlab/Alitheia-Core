#include "metric.h"

#include "core.h"
#include "dbobject.h"

#include <CORBA.h>

using namespace Alitheia;

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
    std::string name;
    int id;
};

AbstractMetric::AbstractMetric()
    : d( new Private( this ) )
{
}

AbstractMetric::~AbstractMetric()
{
    if( d->id != -1 )
        Core::instance()->unregisterMetric( this );
    delete d;
}

CORBA::Boolean AbstractMetric::doInstall()
{
    return install();
}

CORBA::Boolean AbstractMetric::doRemove()
{
    return remove();
}

CORBA::Boolean AbstractMetric::doUpdate()
{
    return update();
}

bool AbstractMetric::install()
{
    return false;
}

bool AbstractMetric::remove()
{
    return false;
}

bool AbstractMetric::update()
{
    return false;
}

char* AbstractMetric::getAuthor()
{
    return CORBA::string_dup( author().c_str() );
}

char* AbstractMetric::getDescription()
{
    return CORBA::string_dup( description().c_str() );
}

char* AbstractMetric::getName()
{
    return CORBA::string_dup( name().c_str() );
}

char* AbstractMetric::getVersion()
{
    return CORBA::string_dup( version().c_str() );
}

char* AbstractMetric::getDateInstalled()
{
    return CORBA::string_dup( dateInstalled().c_str() );
}

const std::string& AbstractMetric::orbName() const
{
    return d->name;
}

void AbstractMetric::setOrbName( const std::string& orbName )
{
    d->name = orbName;
}

int AbstractMetric::id() const
{
    return d->id;
}

void AbstractMetric::setId( int id )
{
    d->id = id;
}

std::vector<Metric> AbstractMetric::getSupportedMetrics() const
{
    return Core::instance()->getSupportedMetrics( this );
}

bool AbstractMetric::addSupportedMetrics( const std::string& description, MetricType::Type type )
{
    return Core::instance()->addSupportedMetrics( this, description, type );
}

char* ProjectVersionMetric::getResult( const alitheia::ProjectVersion& projectVersion )
{
    return CORBA::string_dup( getResult( ProjectVersion( projectVersion ) ).c_str() );
}

void ProjectVersionMetric::run( const alitheia::ProjectVersion& projectVersion )
{
//    return CORBA::string_dup( getResult( ProjectVersion( projectVersion ) ).c_str() );
}

CORBA::Boolean ProjectVersionMetric::run2nd( const alitheia::ProjectVersion& a, const alitheia::ProjectVersion& b )
{
//    return CORBA::string_dup( getResult( ProjectVersion( projectVersion ) ).c_str() );
    return false;
}

char* ProjectFileMetric::getResult( const alitheia::ProjectFile& projectFile )
{
    ProjectFile file( projectFile );
    return CORBA::string_dup( getResult( file ).c_str() );
}

void ProjectFileMetric::run( const alitheia::ProjectFile& projectFile )
{
    ProjectFile file( projectFile );
    run( file );
}

char* StoredProjectMetric::getResult( const alitheia::StoredProject& storedProject )
{
    return CORBA::string_dup( getResult( StoredProject( storedProject ) ).c_str() );
}

void StoredProjectMetric::run( const alitheia::StoredProject& storedProject )
{
//    return CORBA::string_dup( getResult( StoredProject( storedProject ) ).c_str() );
}

char* FileGroupMetric::getResult( const alitheia::FileGroup& fileGroup )
{
    return CORBA::string_dup( getResult( FileGroup( fileGroup ) ).c_str() );
}

void FileGroupMetric::run( const alitheia::FileGroup& fileGroup )
{
//    return CORBA::string_dup( getResult( FileGroup( fileGroup ) ).c_str() );
}
