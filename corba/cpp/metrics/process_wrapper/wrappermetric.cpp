#include "wrappermetric.h"

#include <Core>

#include "wrappermetricjob.h"

#include <cassert>
#include <sstream>

using namespace Alitheia;

using std::cout;
using std::endl;
using std::string;
using std::vector;

ProjectFileWrapperMetric::ProjectFileWrapperMetric( const string& metric, const string& program, 
                                                    const vector< string >& arguments )
    : logger( Logger::NameSqoOssMetric ),
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

string ProjectFileWrapperMetric::getResult( const ProjectFile& ) const
{
    return "getResult";
}

void ProjectFileWrapperMetric::run( ProjectFile& file )
{
    logger << name() << ": Measuring " << file.name << endl;
    Job* job = new ProjectFileWrapperMetricJob( this, program, arguments, file );
    scheduler.enqueueJob( job );
    job->waitForFinished();
}

ProjectVersionWrapperMetric::ProjectVersionWrapperMetric( const string& metric, const string& program, 
                                                          const vector< string >& arguments )
    : logger( Logger::NameSqoOssMetric ),
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

string ProjectVersionWrapperMetric::getResult( const ProjectVersion& ) const
{
    return "getResult";
}

void ProjectVersionWrapperMetric::run( ProjectVersion& version )
{
    logger << name() << ": Measuring " << version.project.name << ", version " << version.version << endl;
    Job* job = new ProjectVersionWrapperMetricJob( this, program, arguments, version );
    scheduler.enqueueJob( job );
    job->waitForFinished();
}
