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

vector< ResultEntry > WcMetric::getResult( const ProjectFile& f, const Metric& m ) const
{
    boost::mutex::scoped_lock lock( mutex );
    vector< ResultEntry > result;
    
    Database::property_map properties;
    properties[ "projectFile" ] = f;
    properties[ "metric" ] = m;
    const vector< ProjectFileMeasurement > measurements = db.findObjectsByProperties< ProjectFileMeasurement >( properties );
    
    if( !measurements.empty() )
    {
        stringstream ss;
        ss << measurements.front().result;
        int value;
        ss >> value;
        result.push_back( ResultEntry( value, ResultEntry::MimeTypeTypeInteger, m.mnemonic ) );
    }
    
    return result;
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
    
    stringstream ss;
    ss << count;
    m.result = ss.str();
    db.addRecord( m );
}
