#include "wrappermetricjob.h"

#include <Metric>
#include <Database>
#include <FDS>
#include <Logger>

#include "temporaryfile.h"

#include <boost/date_time/posix_time/posix_time.hpp>
#include <boost/process.hpp>
#include <boost/bind.hpp>

#include <algorithm>

using std::find;
using std::for_each;
using std::vector;
using std::string;
using std::stringstream;
using std::ostringstream;

using namespace boost::posix_time;
using namespace boost::process;
using namespace boost;

using namespace Alitheia;

ProjectFileWrapperMetricJob::ProjectFileWrapperMetricJob( const ProjectFileWrapperMetric* metric, const string& program, 
                                                          const vector< string >& arguments, const ProjectFile& file )
    : metric( metric ),
      projectFile( file ),
      program( program ),
      arguments( arguments )
{
}

ProjectFileWrapperMetricJob::~ProjectFileWrapperMetricJob()
{
}

void ProjectFileWrapperMetricJob::run()
{
    Logger logger( Logger::Logger::NameSqoOssMetric );
    logger.setTeeStream( std::cout );

    string result;

    // program reads from file
    if( find( arguments.begin(), arguments.end(), "%file%" ) != arguments.end() )
    { 
        TemporaryFile file( projectFile.name.c_str() );
        vector< string > command;
        command.push_back( program );

        for( vector< string >::const_iterator it = arguments.begin(); it != arguments.end(); ++it )
        {
            if( *it != "%file%" )
                command.push_back( *it );
            else
                command.push_back( file.name() );
        }

        projectFile.save( file );
        file.close();

        launcher l;
        command_line cl = command_line::shell( join( command, " " ) );
        l.set_stdout_behavior( redirect_stream );
        child c = l.start( cl );

        pistream& stdout_stream = c.get_stdout();
    
        string line;
        while( std::getline( stdout_stream, line ) )
        {
            result += line + '\n';
        }

        if( !c.wait().exited() )
        {
            logger << "Abnormal program termination." << std::endl;
            return;
        }

    }
    // program reads from standard input
    else
    {
        vector< string > command;
        command.push_back( program );

        for( vector< string >::const_iterator it = arguments.begin(); it != arguments.end(); ++it )
        {
            command.push_back( *it );
        }

        launcher l;
        command_line cl = command_line::shell( join( command, " " ) );
        logger << join( command, " " ) << std::endl;
        l.set_stdout_behavior( redirect_stream );
        l.set_stdin_behavior( redirect_stream );
        child c = l.start( cl );

        pistream& stdout_stream = c.get_stdout();
        postream& stdin_stream = c.get_stdin();
        
        string line;
        do
        {
            std::getline( projectFile, line );
            if( !projectFile.eof() )
                line.push_back( '\n' );
            stdin_stream.write( line.c_str(), line.size() );
        } while( !projectFile.eof() );
    
        stdin_stream.flush();
        stdin_stream.close();

        while( std::getline( stdout_stream, line ) )
        {
            result += line + '\n';
        }

        if( !c.wait().exited() )
        {
            logger << "Abnormal program termination." << std::endl;
            return;
        }
    }

    vector<Metric> metrics = metric->getSupportedMetrics();
    if( metrics.empty() )
        return;

    // add the result
    ProjectFileMeasurement m;
    m.metric = metrics.front();
    m.projectFile = projectFile;
    
    m.result = result;
    Database db;
    db.addRecord( m );
}

void ProjectFileWrapperMetricJob::stateChanged( State state )
{
}

ProjectVersionWrapperMetricJob::ProjectVersionWrapperMetricJob( const ProjectVersionWrapperMetric* metric, const string& program, 
                                                                const vector< string >& arguments, const ProjectVersion& version )
    : metric( metric ),
      projectVersion( version ),
      program( program ),
      arguments( arguments )
{
}

ProjectVersionWrapperMetricJob::~ProjectVersionWrapperMetricJob()
{
}

void ProjectVersionWrapperMetricJob::run()
{
    Logger logger( Logger::Logger::NameSqoOssMetric );
    logger.setTeeStream( std::cout );

    // get a checkout
    FDS fds;
    Checkout co = fds.getCheckout( projectVersion );

    // save it
    TemporaryDirectory dir( "tempco" );
    co.save( dir.name() );

    // run the program on it
    bool gotDirectory = false;
    vector< string > command;
    command.push_back( program );

    for( vector< string >::const_iterator it = arguments.begin(); it != arguments.end(); ++it )
    {
        if( *it != "%directory%" )
        {
            command.push_back( *it );
        }
        else
        {
            command.push_back( dir.name() );
            gotDirectory = true;
        }
    }
    if( !gotDirectory )
        command.push_back( dir.name() );

    launcher l;
    command_line cl = command_line::shell( join( command, " " ) );
    l.set_stdout_behavior( redirect_stream );
    child c = l.start( cl );

    pistream& stdout_stream = c.get_stdout();

    string line;
    string result;
    while( std::getline( stdout_stream, line ) )
    {
        result += line + '\n';
    }

    logger << "Running " << join( command, " " ) << std::endl;

    if( !c.wait().exited() )
    {
        logger << "Abnormal program termination." << std::endl;
        return;
    }


    vector<Metric> metrics = metric->getSupportedMetrics();
    if( metrics.empty() )
        return;

    // add the result
    ProjectVersionMeasurement m;
    m.metric = metrics.front();
    m.projectVersion = projectVersion;
    m.result = result;
    Database db;
    db.addRecord( m );
}

void ProjectVersionWrapperMetricJob::stateChanged( State state )
{
}
