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
    return CORBA::string_dup( "Metric::getAuthor()" );
}

char* Metric::getDescription()
{
    return CORBA::string_dup( "Metric::getDescription()" );
}

char* Metric::getName()
{
    return CORBA::string_dup( "Metric::getName()" );
}

char* Metric::getVersion()
{
    return CORBA::string_dup( "Metric::getVersion()" );
}

char* Metric::getResult()
{
    return CORBA::string_dup( "Metric::getResult()" );
}

char* Metric::getDateInstalled()
{
    return CORBA::string_dup( "Metric::getDateInstalled()" );
}

const std::string& Metric::name() const
{
    return d->name;
}

void Metric::setName( const std::string& name )
{
    d->name = name;
}

int Metric::id() const
{
    return d->id;
}

void Metric::setId( int id )
{
    d->id = id;
}
