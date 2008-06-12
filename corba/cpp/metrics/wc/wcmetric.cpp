#include "wcmetric.h"

#include <Core>
#include <Database>

#include <sstream>
#include <vector>
#include <boost/date_time/posix_time/posix_time.hpp>

using namespace Alitheia;

using namespace boost::posix_time;

using std::endl;
using std::string;
using std::stringstream;
using std::vector;

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
    {
        boost::mutex::scoped_lock lock( mutex );
        logger << name() << ": measuring " << file.name << endl;
    }

    if( file.isDirectory )
        return;
    
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
    
    m.whenRun = to_iso_string( second_clock::local_time() );
    stringstream ss;
    ss << count;
    m.result = ss.str();
    Database db;
    db.addRecord( m );
}
