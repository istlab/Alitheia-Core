#include "wcmetric.h"

#include <Core>

#include "wcmetricjob.h"

#include <sstream>

using namespace Alitheia;

using std::endl;
using std::string;

WcMetric::WcMetric()
    : logger( Logger::NameSqoOssMetric )
{
    logger.setTeeStream( std::cout );
}

bool WcMetric::install()
{
    logger << name() << ": installing" << endl;
    return addSupportedMetrics( description(), "LOC", MetricType::SourceCode );
}

string WcMetric::name() const
{
    return "CORBA Wc metric";
}

string WcMetric::author() const
{
    return "Christoph Schleifenbaum - KDAB";
}

string WcMetric::description() const
{
    return "Line counting metric via CORBA";
}

string WcMetric::version() const
{
    return "0.0.1";
}

string WcMetric::getResult( const ProjectFile& ) const
{
    return "getResult";
}

void WcMetric::run( ProjectFile& file )
{
    logger << name() << ": Measuring " << file.name << endl;
    Core::instance()->enqueueJob( new WcMetricJob( this, file ) );
}
