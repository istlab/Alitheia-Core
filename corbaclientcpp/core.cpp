#include "core.h"
#include "corbahandler.h"

using namespace std;
using namespace Alitheia;

Core::Core()
{
    m_core = alitheia::Core::_narrow( CorbaHandler::instance()->getObject( "Core" ) );
}

Core::~Core()
{
}

int Core::registerMetric( const std::string& name, const alitheia::Metric_var& metric )
{
    CorbaHandler::instance()->exportObject( metric, name.c_str() );
    const int id = m_core->registerMetric( name.c_str() );
    m_registeredServices[ id ] = name;
    return id;
}

void Core::unregisterMetric( int id )
{
    m_core->unregisterMetric( id );
    m_registeredServices.erase( id );
}
