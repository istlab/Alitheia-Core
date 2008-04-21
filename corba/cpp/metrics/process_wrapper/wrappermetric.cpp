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

template< typename T>
T join( const vector< T >& v, const T& t = T() )
{
    T result;
    if( v.empty() )
        return result;
    for( typename vector< T >::const_iterator it = v.begin(); it != v.end() - 1; ++it )
    {
        result += *it;
        result += t;
    }
    result += v.back();

    return result;
}

template< typename T, typename C>
T join( const vector< T >& v, const C& c )
{
    return join( v, T( c ) );
}

ProjectFileWrapperMetric::ProjectFileWrapperMetric( const string& program, const vector< string >& arguments )
    : logger( Logger::NameSqoOssMetric ),
      program( program ),
      arguments( arguments )
{
    logger.setTeeStream( cout );
}

bool ProjectFileWrapperMetric::install()
{
    logger << name() << ": installing" << endl;
    return addSupportedMetrics( description(), "", MetricType::SourceCode );
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
    Core::instance()->enqueueJob( new ProjectFileWrapperMetricJob( this, program, arguments, file ) );
}

ProjectVersionWrapperMetric::ProjectVersionWrapperMetric( const string& program, const vector< string >& arguments )
    : logger( Logger::NameSqoOssMetric ),
      program( program ),
      arguments( arguments )
{
    logger.setTeeStream( cout );
}

bool ProjectVersionWrapperMetric::install()
{
    logger << name() << ": installing" << endl;
    return addSupportedMetrics( description(), "", MetricType::SourceCode );
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
    Core::instance()->enqueueJob( new ProjectVersionWrapperMetricJob( this, program, arguments, version ) );
}
