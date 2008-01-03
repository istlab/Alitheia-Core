#include "logger.h"
#include "core.h"
#include "metric.h"

using namespace Alitheia;

int main( int argc, char **argv)
{
    Core c;
    Logger l( Logger::NameSqoOssMetric );
    l.info( "Registering C++ client metric..." );
    Metric* m = new Metric;
    c.registerMetric( "MyCorbaMetric", m );
    l.info( "C++ client metric registered" );
    c.run();
}
