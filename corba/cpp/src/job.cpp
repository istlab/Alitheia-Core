#include "job.h"

#include <CORBA.h>

#include <iostream>

#include "core.h"

namespace Alitheia
{
    class Job::Private
    {
    public:
        Private( Job* q );
        ~Private();

    private:
        Job* q;

    public:
        std::string name;
        Job::State state;
    };
}

using namespace Alitheia;

Job::Private::Private( Job* q )
    : q( q )
{
}

Job::Private::~Private()
{
}

Job::Job()
    : d( new Private( this ) )
{
}

Job::~Job()
{
    delete d;
}

CORBA::Long Job::priority()
{
    return 0;
}

void Job::run()
{
}

Job::State Job::state() const
{
    return d->state;
}

void Job::stateChanged( State )
{
}

void Job::setState( State state )
{
    if( d->state == state )
        return;

    d->state = state;

    stateChanged( state );
}

void Job::addDependency( Job* other )
{
    Core::instance()->addJobDependency( this, other );
}

void Job::waitForFinished()
{
    Core::instance()->waitForJobFinished( this );
}

const std::string& Job::name() const
{
    return d->name;
}

void Job::setName( const std::string& name )
{
    d->name = name;
}

std::ostream& operator<<( std::ostream& stream, Job::State state )
{
    switch( state )
    {
    case ::alitheia::Job::Created:
        stream << "Created";
        break;
    case ::alitheia::Job::Error:
        stream << "Error";
        break;
    case ::alitheia::Job::Finished:
        stream << "Finished";
        break;
    case ::alitheia::Job::Queued:
        stream << "Queued";
        break;
    case ::alitheia::Job::Running:
        stream << "Running";
        break;
    }
    return stream;
}
