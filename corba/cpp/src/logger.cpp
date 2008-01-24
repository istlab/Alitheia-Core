#include "logger.h"

#include "corbahandler.h"

#include <iostream>
#include <exception>

using namespace std;
using namespace Alitheia;

const std::string Logger::NameSqoOss            = "sqooss";
const std::string Logger::NameSqoOssService     = "sqooss.service";
const std::string Logger::NameSqoOssDatabase    = "sqooss.database";
const std::string Logger::NameSqoOssSecurity    = "sqooss.security";
const std::string Logger::NameSqoOssMessaging   = "sqooss.messaging";
const std::string Logger::NameSqoOssWebServices = "sqooss.webservices";
const std::string Logger::NameSqoOssScheduling  = "sqooss.scheduler";
const std::string Logger::NameSqoOssUpdater     = "sqooss.updater";
const std::string Logger::NameSqoOssWebAdmin    = "sqooss.webadmin";
const std::string Logger::NameSqoOssTDS         = "sqooss.tds";
const std::string Logger::NameSqoOssFDS         = "sqooss.fds";
const std::string Logger::NameSqoOssMetric      = "sqooss.metric";
const std::string Logger::NameSqoOssTester      = "sqooss.tester";


class LoggerBuffer : public streambuf
{
public:
    LoggerBuffer( Logger* logger )
        : streambuf(),
          logger( logger )
    {
    }
    ~LoggerBuffer()
    {
    }

protected:
    int overflow( int c )
    {
        if( c != EOF )
        {
            if( c == 10 )
            {
                logger->info( s );
                s = string();
                return 0;
            }
            s.append( (char*)&c );
        }
        return 0;
    }

private:
    string s;
    Logger* const logger;
};

Logger::Logger( const string& name )
    : ostream( new LoggerBuffer( this ) ),
      m_name( name ),
      copy_stream( 0 )
{
    try
    {
        m_logger = alitheia::Logger::_narrow( CorbaHandler::instance()->getObject( "Logger" ) );
    }
    catch( ... )
    {
        cerr << "Got an exception while getting an instance of the Logger. Make sure the Alitheia system is running and eu.sqooss.service.corbaservice is loaded." << endl;
        throw exception();
    }
}

Logger::~Logger()
{
    delete rdbuf();
}

void Logger::debug( const std::string& message )
{
    try
    {
        copyMessage( message );
        m_logger->debug( m_name.c_str(), message.c_str() );
    }
    catch( ... )
    {
        cerr << "Got an exception while getting an instance of the Logger. Make sure the Alitheia system is running and eu.sqooss.service.corbaservice is loaded." << endl;
    }
}

void Logger::info( const std::string& message )
{
    try
    {
        copyMessage( message );
        m_logger->info( m_name.c_str(), message.c_str() );
    }
    catch( ... )
    {
        cerr << "Got an exception while getting an instance of the Logger. Make sure the Alitheia system is running and eu.sqooss.service.corbaservice is loaded." << endl;
    }
}

void Logger::warn( const std::string& message )
{
    try
    {
        copyMessage( message );
        m_logger->warn( m_name.c_str(), message.c_str() );
    }
    catch( ... )
    {
        cerr << "Got an exception while getting an instance of the Logger. Make sure the Alitheia system is running and eu.sqooss.service.corbaservice is loaded." << endl;
    }
}

void Logger::error( const std::string& message )
{
    try
    {
        copyMessage( message );
        m_logger->error( m_name.c_str(), message.c_str() );
    }
    catch( ... )
    {
        cerr << "Got an exception while getting an instance of the Logger. Make sure the Alitheia system is running and eu.sqooss.service.corbaservice is loaded." << endl;
    }
}

string Logger::name() const
{
    return m_name;
}

void Logger::copyMessage( const string& message )
{
    if( copy_stream != 0 )
        *copy_stream << message << endl;
}

void Logger::setTeeStream( ostream& stream )
{
    copy_stream = &stream;
}

/*ostream& Logger::put( char c )
{
    cerr << c;
    //ss.put( c );
    //return ostream::put( c );
    //return *this;
}*/

/*Logger& Logger::operator<<( std::ostream& (*f)(std::ostream&) )
{
    // flush
    info( ss.str() );
    // and clear
    //ss = stringstream();
    return *this;
}

Logger& Logger::operator<<( const std::string& message )
{
    ss << message;
    return *this;
}*/
