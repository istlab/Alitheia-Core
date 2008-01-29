#include "core.h"

#include "corbahandler.h"
#include "job.h"
#include "metric.h"

#include <string>
#include <sstream>

namespace Alitheia
{
    class Core::Private
    {
    public:
        Private( Core* q );

    private:
        Core* q;

    public:
        alitheia::Core_var core;
        std::map< int, std::string > registeredServices;
    };
}

using namespace std;
using namespace Alitheia;

Core::Private::Private( Core* q )
    : q( q )
{
};

Core::Core()
    : d( new Private( this ) )
{
    try
    {
        d->core = alitheia::Core::_narrow( CorbaHandler::instance()->getObject( "Core" ) );
    }
    catch( CORBA::SystemException_catch& ex )
    {
        cerr << "Exception in Core::Core():" << endl;
        ex->_print( cerr );
        cerr << endl;
        throw ex;
    }
    catch( ... )
    {
        cerr << "dumdidum?" << endl;
    }
}

Core::~Core()
{
    delete d;
}

Core* Core::instance()
{
    static Core core;
    return &core;
}

int Core::registerMetric( const std::string& name, Metric* metric )
{
    CorbaHandler::instance()->exportObject( metric->_this(), name.c_str() );
    const int id = d->core->registerMetric( CORBA::string_dup( name.c_str() ) );
    d->registeredServices[ id ] = name;
    return id;
}

void Core::unregisterMetric( int id )
{
    d->core->unregisterMetric( id );
    d->registeredServices.erase( id );
}

int Core::registerJob( Job* job )
{
    static int jobCount = 0;
    std::stringstream ss;
    ss << "Alitheia_Job_" << ++jobCount;
    const std::string name = ss.str();
    job->setName( name );
    CorbaHandler::instance()->exportObject( job->_this(), name.c_str() );
    const int id = d->core->registerJob( CORBA::string_dup( name.c_str() ) );
    d->registeredServices[ id ] = name;
    return id;
}

void Core::enqueueJob( Job* job )
{
    if( job->name().length() == 0 )
        registerJob( job );
    d->core->enqueueJob( CORBA::string_dup( job->name().c_str() ) );
}

void Core::addJobDependency( Job* job, Job* dependency )
{
    if( job->name().length() == 0 )
        registerJob( job );
    if( dependency->name().length() == 0 )
        registerJob( dependency );
    d->core->addJobDependency( CORBA::string_dup( job->name().c_str() ),
                               CORBA::string_dup( dependency->name().c_str() ) );
}

void Core::waitForJobFinished( Job* job )
{
    if( job->name().length() == 0 )
        registerJob( job );
    d->core->waitForJobFinished( CORBA::string_dup( job->name().c_str() ) );
}

void Core::run()
{
    CorbaHandler::instance()->run();
}
