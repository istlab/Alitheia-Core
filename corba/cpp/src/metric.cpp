#include "metric.h"
#include "core.h"

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

char* ProjectVersionMetric::getResult( const alitheia::ProjectVersion& projectVersion )
{
    return CORBA::string_dup( getResult( ProjectVersion( projectVersion ) ).c_str() );
}
