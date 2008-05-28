#include "wcmetricjob.h"

#include <Metric>
#include <Database>
#include <Logger>
#include <boost/date_time/posix_time/posix_time.hpp>

using std::vector;
using std::string;
using std::stringstream;
using namespace boost::posix_time;

using namespace Alitheia;

WcMetricJob::WcMetricJob( const AbstractMetric* metric, const ProjectFile& file )
    : metric( metric ),
      projectFile( file )
{
}

WcMetricJob::~WcMetricJob()
{
}

void WcMetricJob::run()
{
    Logger logger;
    logger.setTeeStream( std::cout );
    logger << name() << ": Measuring " << projectFile.name << std::endl;
    string line;
    int count = -1;
    do
    {
        ++count;
        std::getline( projectFile, line );
    } while( !projectFile.eof() );

    vector<Metric> metrics = metric->getSupportedMetrics();
    if( metrics.empty() )
        return;

    // add the result
    ProjectFileMeasurement m;
    m.metric = metrics.front();
    m.projectFile = projectFile;
    
    m.whenRun = to_iso_string( second_clock::local_time() );
    stringstream ss;
    ss << count;
    m.result = ss.str();
    Database db;
    db.addRecord( m );
}

void WcMetricJob::stateChanged( State state )
{
}
