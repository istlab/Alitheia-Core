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

namespace Alitheia
{
    class Logger::Private
    {
    public:
        class LoggerBuffer : public streambuf
        {
        public:
            LoggerBuffer( Logger* logger );

        protected:
            int overflow( int c );

        private:
            string s;
            Logger* const logger;
        };

        Private( Logger* q );

        void copyMessage( const string& message );
    
    private:
        Logger* q;

    public:
        string name;
        alitheia::Logger_var logger;
        ostream* copy_stream;
    };
}
   
Logger::Private::LoggerBuffer::LoggerBuffer( Logger* logger )
    : streambuf(),
      logger( logger )
{
}

int Logger::Private::LoggerBuffer::overflow( int c )
{
    if( c == EOF )
        return 0;
    if( c == 10 )
    {
        logger->info( s );
        s = string();
        return 0;
    }
    s.append( (char*)&c );
    return 0;
}

Logger::Private::Private( Logger* q )
    : q( q ),
      copy_stream( 0 )
{
}

void Logger::Private::copyMessage( const string& message )
{
    if( copy_stream != 0 )
        *copy_stream << message << endl;
}


Logger::Logger( const string& name )
    : ostream( new Private::LoggerBuffer( this ) ),
      d( new Private( this ) )
{
    d->name = name;
    try
    {
        d->logger = alitheia::Logger::_narrow( CorbaHandler::instance()->getObject( "Logger" ) );
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
        d->copyMessage( message );
        d->logger->debug( d->name.c_str(), message.c_str() );
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
        d->copyMessage( message );
        d->logger->info( d->name.c_str(), message.c_str() );
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
        d->copyMessage( message );
        d->logger->warn( d->name.c_str(), message.c_str() );
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
        d->copyMessage( message );
        d->logger->error( d->name.c_str(), message.c_str() );
    }
    catch( ... )
    {
        cerr << "Got an exception while getting an instance of the Logger. Make sure the Alitheia system is running and eu.sqooss.service.corbaservice is loaded." << endl;
    }
}

string Logger::name() const
{
    return d->name;
}

void Logger::setTeeStream( ostream& stream )
{
    d->copy_stream = &stream;
}
