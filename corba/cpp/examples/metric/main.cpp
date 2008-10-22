#include <Logger>
#include <Core>
#include <Metric>
#include <DBObject>
#include <Database>
#include <FDS>

#include <sstream>
#include <ostream>
#include <vector>

using namespace std;
using namespace Alitheia;

class MyMetric : virtual public ProjectFileMetric
{
public:
    bool install()
    {
        return addSupportedMetrics( description(), "Test", MetricType::SourceCode );
    }

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

    std::vector< ResultEntry > getResult( const ProjectFile&, const Metric& m ) const
    {
        return std::vector< ResultEntry >();
    }

    void run( ProjectFile& file )
    {
        Logger logger( Logger::NameSqoOssMetric );
        logger.setTeeStream( cout );
        logger << "MyMetric: Measuring " << file.name << endl;
        string line;
        int count = -1;
        do
        {
            ++count;
            std::getline( file, line );
        } while( !file.eof() );

        vector<Metric> metrics = getSupportedMetrics();
        if( metrics.empty() )
            return;

        // add the result
        ProjectFileMeasurement m;
        m.metric = metrics.front();
        m.projectFile = file;
        stringstream ss;
        ss << count;
        m.result = ss.str();
        db.addRecord( m );
    }

private:
    Database db;
};

#include <vector>

int main( int argc, char **argv)
{
    Core& c = *Core::instance();
   
    const StoredProject p = StoredProject::getProjectByName( "SVN" );
    cout << p.id << " " << p.name << endl;

    const ProjectVersion v = StoredProject::getLastProjectVersion( p );
    cout << v.id << " " << v.version << endl;

    FDS fds;
    Checkout co = fds.getCheckout( v, "/mirror/.*" );
    
    for( vector< ProjectFile >::iterator it = co.files.begin(); it != co.files.end(); ++it )
    {
        cout << it->getFileName() << endl;
        do
        {
            std::string line;
            std::getline( *it, line );
            cout << line << endl;
        } while( !it->eof() );
    }

    return 0;

    Logger logger( Logger::NameSqoOssMetric );
    logger.setTeeStream( cout );
    
    MyMetric* m = new MyMetric;
    logger << "Registering C++ client metric..." << endl;
    int id = c.registerMetric( m );
    logger << "C++ client metric registered, id is " << id << "." << endl;

    logger << "Metric waiting for orders..." << endl;
    
    c.run();
}
