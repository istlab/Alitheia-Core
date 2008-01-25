#include "core.h"

#include "corbahandler.h"
#include "job.h"
#include "metric.h"

#include <string>

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

int Core::registerJob( const std::string& name, Job* job )
{
    CorbaHandler::instance()->exportObject( job->_this(), name.c_str() );
    const int id = d->core->registerJob( CORBA::string_dup( name.c_str() ) );
    d->registeredServices[ id ] = name;
    return id;
}

void Core::unregisterJob( int id )
{
    d->core->unregisterJob( id );
    d->registeredServices.erase( id );
}

void Core::run()
{
    CorbaHandler::instance()->run();
}
