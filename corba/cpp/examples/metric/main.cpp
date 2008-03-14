#include <Logger>
#include <Core>
#include <Metric>
#include <DBObject>

#include <sstream>
#include <ostream>

using namespace std;
using namespace Alitheia;

template< class METRIC >
class MyMetric : virtual public METRIC
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

    string getResult( const ProjectFile& ) const
    {
        return "getResult";
    }

    string getResult( const ProjectVersion& ) const
    {
        return "getResult";
    }

    string getResult( const StoredProject& ) const
    {
        return "getResult";
    }

    string getResult( const FileGroup& ) const
    {
        return "getResult";
    }

    void run( ProjectFile& file ) const
    {
        Logger logger( Logger::NameSqoOssMetric );
        logger.setTeeStream( cout );
        logger << "MyMetric::run: " << file.name << endl;
        logger << "MyMetric::run: " << file.status << endl;
        logger << "MyMetric::run: " << file.projectVersion.version << endl;
        string line;
        std::getline( file, line );
        logger << "MyMetric::run: First line: " << line << endl;
    }
};

int main( int argc, char **argv)
{
    Core& c = *Core::instance();
    
    Logger logger( Logger::NameSqoOssMetric );
    logger.setTeeStream( cout );
    
    MyMetric< ProjectFileMetric>* m = new MyMetric< ProjectFileMetric >;
    logger << "Registering C++ client metric..." << endl;
    int id = c.registerMetric( m );
    logger << "C++ client metric registered, id is " << id << "." << endl;

/*    m = new MyMetric< ProjectVersionMetric >;
    logger << "Registering C++ client metric..." << endl;
    id = c.registerMetric( m );
    logger << "C++ client metric registered, id is " << id << "." << endl;


    m = new MyMetric< FileGroupMetric >;
    logger << "Registering C++ client metric..." << endl;
    id = c.registerMetric( m );
    logger << "C++ client metric registered, id is " << id << "." << endl;


    m = new MyMetric< StoredProjectMetric >;
    logger << "Registering C++ client metric..." << endl;
    id = c.registerMetric( m );
    logger << "C++ client metric registered, id is " << id << "." << endl;*/


    logger << "Metrics waiting for orders..." << endl;
    
    c.run();
}
