#include "scheduler.h"

#include "corbahandler.h"
#include "job.h"
#include "core.h"

#include "Alitheia.h"

#include <algorithm>
#include <exception>
#include <fstream>
#include <vector>
#include <sstream>

namespace Alitheia
{
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

Scheduler::~Scheduler()
{
    for( vector< string >::iterator it = d->registeredJobs.begin(); it != d->registeredJobs.end(); ++it )
        unregisterJob( *it );
    delete d;
}

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

bool Scheduler::isExecuting() const
{
    return d->scheduler->isExecuting();
}

void Scheduler::startExecute( int n )
{
    d->scheduler->startExecute( n );
}

void Scheduler::stopExecute()
{
    d->scheduler->stopExecute();
}

void Scheduler::unregisterJob( const string& name )
{
    d->scheduler->unregisterJob( CORBA::string_dup( name.c_str() ) );
    CorbaHandler::instance()->unexportObject( CORBA::string_dup( name.c_str() ) );
    d->registeredJobs.erase( std::find( d->registeredJobs.begin(), d->registeredJobs.end(), name ) );
}

void Scheduler::unregisterJob( Job* job )
{
    unregisterJob( job->name() );
}

void Scheduler::enqueueJob( Job* job )
{
    if( job->name().length() == 0 )
        registerJob( job );
    d->scheduler->enqueueJob( CORBA::string_dup( job->name().c_str() ) );
}

void Scheduler::addJobDependency( Job* job, Job* dependency )
{
    if( job->name().length() == 0 )
        registerJob( job );
    if( dependency->name().length() == 0 )
        registerJob( dependency );
    d->scheduler->addJobDependency( CORBA::string_dup( job->name().c_str() ),
                               CORBA::string_dup( dependency->name().c_str() ) );
}

void Scheduler::waitForJobFinished( Job* job )
{
    if( job->name().length() == 0 )
        registerJob( job );
    d->scheduler->waitForJobFinished( CORBA::string_dup( job->name().c_str() ) );
}
