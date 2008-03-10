#include <Logger>
#include <Core>
#include <Metric>

#include <sstream>
#include <ostream>

using namespace std;
using namespace Alitheia;

class MyMetric : public ProjectVersionMetric
{
public:
    string name() const
    {
        return "Example CORBA metric";
    }

    string author() const
    {
        return "Max Mustermann";
    }

    string description() const
    {
        return "This is just an example about how to put CORBA metrics into alitheia.";
    }

    string version() const
    {
        return "1.0.0.0";
    }

    string result() const
    {
        return string();
    }

    string dateInstalled() const
    {
        return string();
    }

    string getResult( const ProjectVersion& ) const
    {
        return "getResult";
    }
};

int main( int argc, char **argv)
{
    Core& c = *Core::instance();
    
    Logger logger( Logger::NameSqoOssMetric );
    logger.setTeeStream( cout );
    
    AbstractMetric* const m = new MyMetric;
    logger << "Registering C++ client metric..." << endl;
    
    const int id = c.registerMetric( m );
    logger << "C++ client metric registered, id is " << id << "." << endl;
    logger << "Metric waiting for orders..." << endl;
    
    c.run();
}
