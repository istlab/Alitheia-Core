#include <Logger>
#include <Core>
#include <Metric>

#include <sstream>
#include <ostream>

using namespace std;
using namespace Alitheia;

int main( int argc, char **argv)
{
    Core c;
    
    Logger logger( Logger::NameSqoOssMetric );
    logger.setTeeStream( cout );
    
    Metric* const m = new Metric;
    logger << "Registering C++ client metric..." << endl;
    
    const int id = c.registerMetric( "MyCorbaMetric", m );
    logger << "C++ client metric registered, id is " << id << "." << endl;
    logger << "Metric waiting for orders..." << endl;
    
    c.run();
}
