#include <Logger>
#include <Core>
#include <Metric>

#include <sstream>
#include <ostream>

using namespace std;
using namespace Alitheia;

class MyMetric : public Metric
{
};

int main( int argc, char **argv)
{
    Core& c = *Core::instance();
    
    Logger logger( Logger::NameSqoOssMetric );
    logger.setTeeStream( cout );
    
    Metric* const m = new MyMetric;
    logger << "Registering C++ client metric..." << endl;
    
    const int id = c.registerMetric( m );
    logger << "C++ client metric registered, id is " << id << "." << endl;
    logger << "Metric waiting for orders..." << endl;
    
    c.run();
}
