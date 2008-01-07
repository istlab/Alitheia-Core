#include "logger.h"
#include "core.h"
#include "metric.h"

#include <sstream>

using namespace std;
using namespace Alitheia;

int main( int argc, char **argv)
{
    Core c;
    Logger l( Logger::NameSqoOssMetric );
    
    Metric* const m = new Metric;
    l.info( "Registering C++ client metric..." );
    
    const int id = c.registerMetric( "MyCorbaMetric", m );
    stringstream ss;
    ss << "C++ cient metric registered, id is " << id << ".";
    l.info( ss.str() );
    
    c.run();
}
