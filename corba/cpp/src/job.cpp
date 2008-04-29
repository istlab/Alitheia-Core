#include "job.h"

#include <CORBA.h>

#include <vector>

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
using namespace eu::sqooss::impl::service::corba;

Job::Private::Private( Job* q )
    : q( q ),
      state( Job::Created )
{
}

Job::Private::~Private()
{
}

static std::vector< Job* > doomedJobs;

Job::Job()
    : d( new Private( this ) )
{
    this->stateChanged( Created );
}

Job::~Job()
{
    if( d->name.length() != 0 )
        Core::instance()->unregisterJob( this );
    delete d;
}

void* Job::operator new( size_t s )
{
    return malloc( s );
}

void Job::operator delete( void* o )
{
    doomedJobs.push_back( static_cast< Job* >( o ) );
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

void Job::setState( alitheia::Job::JobState state )
{
    if( d->state == static_cast< State >( state ) )
        return;

    d->state = static_cast< State >( state );

    stateChanged( d->state );
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
    case Job::Created:
        stream << "Created";
        break;
    case Job::Error:
        stream << "Error";
        break;
    case Job::Finished:
        stream << "Finished";
        break;
    case Job::Queued:
        stream << "Queued";
        break;
    case Job::Running:
        stream << "Running";
        break;
    default:
        stream << "Undefined";
        break;
    }
    return stream;
}
