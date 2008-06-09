#include "job.h"

#include "scheduler.h"

#include <CORBA.h>

#include <vector>

#include "core.h"

namespace Alitheia
{
    /**
     * \internal
     */
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

        static Scheduler scheduler;
    };
}

Alitheia::Scheduler Alitheia::Job::Private::scheduler;

using namespace Alitheia;
using namespace eu::sqooss::impl::service::corba;

/**
 * \internal
 */
Job::Private::Private( Job* q )
    : q( q ),
      state( Job::Created )
{
}

/**
 * \internal
 */
Job::Private::~Private()
{
}

static std::vector< Job* > doomedJobs;

/**
 * Creates a new Job.
 */
Job::Job()
    : d( new Private( this ) )
{
    this->stateChanged( Created );
}

/**
 * Destroys this Job.
 * The instance is automatically unregistered from
 * the Scheduler, if it was registered.
 */
Job::~Job()
{
    if( d->name.length() != 0 )
        d->scheduler.unregisterJob( this );
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

/**
 * Returns the job's priority.
 * The priority is the order of the jobs being executed.
 * Therefore a lower number here leads to a higher priority.
 */
CORBA::Long Job::priority()
{
    return 0;
}

/**
 * Runs the job. Reimplement this method to do 
 * what ever this job should do.
 * The default implementation does nothing.
 * Note that the jobs are running multithreaded.
 */
void Job::run()
{
}

/**
 * Returns the job's state.
 */
Job::State Job::state() const
{
    return d->state;
}

/**
 * Notifies the job that it's state changed.
 * Reimplement this method if your implementation
 * needs to do something on that event.
 * The default implementation does nothing.
 */
void Job::stateChanged( State )
{
}

/**
 * Set's this jobs state to \a state.
 * Called by the Scheduler via the CORBA ORB.
 */
void Job::setState( alitheia::Job::JobState state )
{
    if( d->state == static_cast< State >( state ) )
        return;

    d->state = static_cast< State >( state );

    stateChanged( d->state );
}

/**
 * Adds the job \a other as dependency to this job.
 * This job will not be executed before \a other has finished.
 */
void Job::addDependency( Job* other )
{
    d->scheduler.addJobDependency( this, other );
}

/** 
 * Waits for this job to finish.
 */
void Job::waitForFinished()
{
    d->scheduler.waitForJobFinished( this );
}

/**
 * Returns this job's name within the CORBA ORB.
 */
const std::string& Job::name() const
{
    return d->name;
}

/**
 * Sets this job's name within the CORBA ORB.
 * Called by the Scheduler upon registration.
 */
void Job::setName( const std::string& name )
{
    d->name = name;
}

/**
 * Streaming operator to print the state of a job.
 */
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
