#include <Logger>
#include <Core>
#include <Metric>

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
    ss << "C++ client metric registered, id is " << id << ".";
    l.info( ss.str() );
   
    l.info( "Metric waiting for orders..." );
    
    c.run();
}
