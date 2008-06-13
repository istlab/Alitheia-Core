#include "wrappermetric.h"

#include "wrappermetricjob.h"

#include <cassert>
#include <sstream>

using namespace Alitheia;

using std::cout;
using std::endl;
using std::string;
using std::vector;

ProjectFileWrapperMetric::ProjectFileWrapperMetric( const string& metric, const string& resultType, const string& program, 
                                                    const vector< string >& arguments )
    : logger( Logger::NameSqoOssMetric ),
      resultType( resultType ),
      metric( metric ),
      program( program ),
      arguments( arguments )
{
    logger.setTeeStream( cout );
}

bool ProjectFileWrapperMetric::install()
{
    logger << name() << ": installing" << endl;
    return addSupportedMetrics( description(), metric, MetricType::SourceCode );
}

string ProjectFileWrapperMetric::name() const
{
    return "CORBA Wrapper for \"" + program + " " + join( arguments, " " ) + "\"";
}

string ProjectFileWrapperMetric::author() const
{
    return "Christoph Schleifenbaum - KDAB";
}

string ProjectFileWrapperMetric::description() const
{
    return "CORBA Wrapper for \"" + program + " " + join( arguments, " " ) + "\"";
}

string ProjectFileWrapperMetric::version() const
{
    return "0.0.1";
}

vector< ResultEntry > ProjectFileWrapperMetric::getResult( const ProjectFile& f, const Metric& metric ) const
{
    vector< ResultEntry > result;
    
    Database::property_map properties;
    properties[ "projectFile" ] = f;
    properties[ "metric" ] = metric;
    const vector< ProjectFileMeasurement > measurements = db.findObjectsByProperties< ProjectFileMeasurement >( properties );
    
    if( !measurements.empty() )
        result.push_back( ResultEntry( measurements.front().result, resultType, metric.mnemonic ) );
    
    return result;
}

void ProjectFileWrapperMetric::run( ProjectFile& file )
{
    logger << name() << ": Measuring " << file.name << endl;
    ProjectFileWrapperMetricJob job( this, program, arguments, file );
    job.run();
}

ProjectVersionWrapperMetric::ProjectVersionWrapperMetric( const string& metric, const string& resultType, const string& program, 
                                                          const vector< string >& arguments )
    : logger( Logger::NameSqoOssMetric ),
      resultType( resultType ),
      metric( metric ),
      program( program ),
      arguments( arguments )
{
    logger.setTeeStream( cout );
}

bool ProjectVersionWrapperMetric::install()
{
    logger << name() << ": installing" << endl;
    return addSupportedMetrics( description(), metric, MetricType::SourceCode );
}

string ProjectVersionWrapperMetric::name() const
{
    return "CORBA Wrapper for \"" + program + " " + join( arguments, " " ) + "\"";
}

string ProjectVersionWrapperMetric::author() const
{
    return "Christoph Schleifenbaum - KDAB";
}

string ProjectVersionWrapperMetric::description() const
{
    return "CORBA Wrapper for \"" + program + " " + join( arguments, " " ) + "\"";
}

string ProjectVersionWrapperMetric::version() const
{
    return "0.0.1";
}

vector< ResultEntry > ProjectVersionWrapperMetric::getResult( const ProjectVersion& v, const Metric& metric ) const
{
    vector< ResultEntry > result;
    
    Database::property_map properties;
    properties[ "projectVersion" ] = v;
    properties[ "metric" ] = metric;
    const vector< ProjectVersionMeasurement > measurements = db.findObjectsByProperties< ProjectVersionMeasurement >( properties );
    
    if( !measurements.empty() )
        result.push_back( ResultEntry( measurements.front().result, resultType, metric.mnemonic ) );
    
    return result;
}

void ProjectVersionWrapperMetric::run( ProjectVersion& version )
{
    logger << name() << ": Measuring " << version.project.name << ", version " << version.version << endl;
    ProjectVersionWrapperMetricJob job( this, program, arguments, version );
    job.run();
}
