#include "wrappermetric.h"

#include <Core>

#include <QCoreApplication>
#include <QStringList>

#include "wrappermetricjob.h"

#include <cassert>
#include <sstream>

using namespace Alitheia;

using std::endl;
using std::string;

ProjectFileWrapperMetric::ProjectFileWrapperMetric()
    : logger( Logger::NameSqoOssMetric )
{
    logger.setTeeStream( std::cout );

    QStringList arg = QCoreApplication::arguments();
    arg.takeFirst();
    assert( !arg.isEmpty() );
    program = arg.takeFirst();
    arguments = arg;
}

bool ProjectFileWrapperMetric::install()
{
    logger << name() << ": installing" << endl;
    return addSupportedMetrics( description(), MetricType::SourceCode );
}

string ProjectFileWrapperMetric::name() const
{
    return "CORBA Wrapper for \"" + program.toStdString() + " " + arguments.join( QChar::fromLatin1( ' ' ) ).toStdString() + "\"";
}

string ProjectFileWrapperMetric::author() const
{
    return "Christoph Schleifenbaum - KDAB";
}

string ProjectFileWrapperMetric::description() const
{
    return "CORBA Wrapper for \"" + program.toStdString() + " " + arguments.join( QChar::fromLatin1( ' ' ) ).toStdString() + "\"";
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
