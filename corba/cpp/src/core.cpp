#include "core.h"

#include "corbahandler.h"
#include "metric.h"

using namespace std;
using namespace Alitheia;

Core::Core()
{
    try
    {
        m_core = alitheia::Core::_narrow( CorbaHandler::instance()->getObject( "Core" ) );
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
}

int Core::registerMetric( const std::string& name, Metric* metric )
{
    CorbaHandler::instance()->exportObject( metric->_this(), name.c_str() );
    const int id = m_core->registerMetric( CORBA::string_dup( name.c_str() ) );
    m_registeredServices[ id ] = name;
    return id;
}

void Core::unregisterMetric( int id )
{
    m_core->unregisterMetric( id );
    m_registeredServices.erase( id );
}

void Core::run()
{
    CorbaHandler::instance()->run();
}
