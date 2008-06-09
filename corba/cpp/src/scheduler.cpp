#include "scheduler.h"

#include "corbahandler.h"
#include "job.h"
#include "core.h"

#include "Alitheia.h"

#include <algorithm>
#include <string>
#include <exception>
#include <fstream>
#include <vector>
#include <sstream>

namespace Alitheia
{
    /**
     * \internal
     */
    class Scheduler::Private
    {
    public:
        Private( Scheduler* q )
            : q( q )
        {
        }
        
    private:
        Scheduler* const q;

    public:
        eu::sqooss::impl::service::corba::alitheia::Scheduler_var scheduler;
        std::vector< std::string > registeredJobs;
    };
}

using namespace Alitheia;
using namespace eu::sqooss::impl::service::corba;
using std::cerr;
using std::endl;
using std::exception;
using std::string;
using std::vector;

/**
 * Creates a new Scheduler instance.
 */
Scheduler::Scheduler()
    : d( new Private( this ) )
{
    try
    {
        d->scheduler = alitheia::Scheduler::_narrow( CorbaHandler::instance()->getObject( "AlitheiaScheduler" ) );
    }
    catch( ... )
    {
         cerr << "Got an exception while getting an instance of the Scheduler. Make sure the Alitheia system is running and eu.sqooss.service.corbaservice is loaded." << endl;
        throw exception();
    }
}

/**
 * Destroys the Scheduler.
 */
Scheduler::~Scheduler()
{
    for( vector< string >::iterator it = d->registeredJobs.begin(); it != d->registeredJobs.end(); ++it )
        unregisterJob( *it );
    delete d;
}

/**
 * Registers \a job in the scheduler.
 */
int Scheduler::registerJob( Job* job )
{
    std::stringstream ss;
    ss << "Alitheia_Job_" << Core::instance()->getUniqueId();
    const std::string name = ss.str();
    job->setName( name );
    CorbaHandler::instance()->exportObject( job->_this(), name.c_str() );
    const int id = d->scheduler->registerJob( CORBA::string_dup( name.c_str() ) );
    d->registeredJobs.push_back( name );
    return id;
}

/**
 * Returns wheter the scheduler is currently executing jobs.
 */
bool Scheduler::isExecuting() const
{
    return d->scheduler->isExecuting();
}

/**
 * Starts \a n more threads for execution of jobs.
 */
void Scheduler::startExecute( int n )
{
    d->scheduler->startExecute( n );
}

/**
 * Stops execution of jobs.
 */
void Scheduler::stopExecute()
{
    d->scheduler->stopExecute();
}

/**
 * Unregisters a job previously registered with \a name
 * from the scheduler.
 */
void Scheduler::unregisterJob( const string& name )
{
    d->scheduler->unregisterJob( CORBA::string_dup( name.c_str() ) );
    CorbaHandler::instance()->unexportObject( CORBA::string_dup( name.c_str() ) );
    d->registeredJobs.erase( std::find( d->registeredJobs.begin(), d->registeredJobs.end(), name ) );
}

/**
 * Unregisters \a job from the scheduler.
 */
void Scheduler::unregisterJob( Job* job )
{
    unregisterJob( job->name() );
}

/**
 * Enqueues \a job in the scheduler.
 */
void Scheduler::enqueueJob( Job* job )
{
    if( job->name().length() == 0 )
        registerJob( job );
    d->scheduler->enqueueJob( CORBA::string_dup( job->name().c_str() ) );
}

/**
 * Adds \a dependency to the list of jobs \a job depends on.
 * \a job will not be started until \a dependency is finished.
 */
void Scheduler::addJobDependency( Job* job, Job* dependency )
{
    if( job->name().length() == 0 )
        registerJob( job );
    if( dependency->name().length() == 0 )
        registerJob( dependency );
    d->scheduler->addJobDependency( CORBA::string_dup( job->name().c_str() ),
                               CORBA::string_dup( dependency->name().c_str() ) );
}

/**
 * Waits for \a job to finish.
 */
void Scheduler::waitForJobFinished( Job* job )
{
    if( job->name().length() == 0 )
        registerJob( job );
    d->scheduler->waitForJobFinished( CORBA::string_dup( job->name().c_str() ) );
}
