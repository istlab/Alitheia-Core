#include "wrappermetricjob.h"

#include <Metric>
#include <Database>
#include <boost/date_time/posix_time/posix_time.hpp>

#include <QProcess>
#include <QTemporaryFile>

using std::vector;
using std::string;
using std::stringstream;
using namespace boost::posix_time;

using namespace Alitheia;

ProjectFileWrapperMetricJob::ProjectFileWrapperMetricJob( const AbstractMetric* metric, const QString& program, 
                                                          const QStringList& arguments, const ProjectFile& file )
    : metric( metric ),
      projectFile( file ),
      program( program ),
      arguments( arguments ),
      process( 0 )
{
}

ProjectFileWrapperMetricJob::~ProjectFileWrapperMetricJob()
{
}

void ProjectFileWrapperMetricJob::run()
{
    QProcess p;
    process = &p;
   
    result.clear();

    connect( &p, SIGNAL( readyReadStandardOutput() ), this, SLOT( readyReadStandardOutput() ) );
    

    if( arguments.contains( "%file%" ) )
    { 
        QTemporaryFile file( QString::fromStdString( projectFile.name ) );
        file.open();
        arguments.replace( arguments.indexOf( "%file%" ), file.fileName() );
        string line;
        do
        {
            std::getline( projectFile, line );
            if( !projectFile.eof() )
                line.push_back( '\n' );
            file.write( line.c_str(), line.size() );
        } while( !projectFile.eof() );
        file.close();
        p.start( program, arguments );
        p.waitForFinished( -1 );
    }
    // program reads from standard input
    else
    {
        p.start( program, arguments );

        string line;
        do
        {
            std::getline( projectFile, line );
            if( !projectFile.eof() )
                line.push_back( '\n' );
            p.write( line.c_str(), line.size() );
        } while( !projectFile.eof() );
    
        p.closeWriteChannel();
        p.waitForFinished( -1 );
    }


    vector<Metric> metrics = metric->getSupportedMetrics();
    if( metrics.empty() )
        return;

    // add the result
    ProjectFileMeasurement m;
    m.metric = metrics.front();
    m.projectFile = projectFile;
    
    m.whenRun = to_iso_string( second_clock::local_time() );
    m.result = result.toStdString();
    Database db;
    db.addRecord( m );
}

void ProjectFileWrapperMetricJob::readyReadStandardOutput()
{
    result += process->readAllStandardOutput();
}

void ProjectFileWrapperMetricJob::stateChanged( State state )
{
    if( state == Finished )
        delete this;
}
