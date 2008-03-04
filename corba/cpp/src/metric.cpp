#include "metric.h"
#include "core.h"

#include <CORBA.h>

using namespace Alitheia;

class Metric::Private
{
public:
    Private( Metric *q )
        : q( q ),
          id( -1 )
    {
    }

private:
    Metric* const q;

public:
    std::string name;
    int id;
};

Metric::Metric()
    : d( new Private( this ) )
{
}

Metric::~Metric()
{
    if( d->id != -1 )
        Core::instance()->unregisterMetric( this );
    delete d;
}

char* Metric::getAuthor()
{
    return CORBA::string_dup( author().c_str() );
}

char* Metric::getDescription()
{
    return CORBA::string_dup( description().c_str() );
}

char* Metric::getName()
{
    return CORBA::string_dup( name().c_str() );
}

char* Metric::getVersion()
{
    return CORBA::string_dup( version().c_str() );
}

char* Metric::getResult()
{
    return CORBA::string_dup( result().c_str() );
}

char* Metric::getDateInstalled()
{
    return CORBA::string_dup( dateInstalled().c_str() );
}

const std::string& Metric::orbName() const
{
    return d->name;
}

void Metric::setOrbName( const std::string& orbName )
{
    d->name = orbName;
}

int Metric::id() const
{
    return d->id;
}

void Metric::setId( int id )
{
    d->id = id;
}
