#include "logger.h"

#include "corbahandler.h"

#include <iostream>
#include <exception>

namespace Alitheia
{
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

    /**
     * \internal
     */
    class Logger::Private
    {
    public:
        /**
         * \internal
         */
        class LoggerBuffer : public std::streambuf
        {
        public:
            LoggerBuffer( Logger* logger );

        protected:
            int overflow( int c );

        private:
            std::string s;
            Logger* const logger;
        };

        Private( Logger* q );

        void copyMessage( const std::string& message );
    
    private:
        Logger* q;

    public:
        std::string name;
        eu::sqooss::impl::service::corba::alitheia::Logger_var logger;
        std::ostream* copy_stream;
    };
}

using namespace Alitheia;
using namespace eu::sqooss::impl::service::corba;
using std::exception;
using std::cerr;
using std::endl;
using std::string;
using std::streambuf;
using std::ostream;

/**
 * \internal
 */
Logger::Private::LoggerBuffer::LoggerBuffer( Logger* logger )
    : streambuf(),
      logger( logger )
{
}

/**
 * \internal
 */
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

/**
 * \internal
 */
Logger::Private::Private( Logger* q )
    : q( q ),
      copy_stream( 0 )
{
}

/**
 * \internal
 */
void Logger::Private::copyMessage( const string& message )
{
    if( copy_stream != 0 )
        *copy_stream << message << endl;
}

/**
 * Creates a new logger with \a name.
 * Only the constant values defined in this class are valid names.
 */
Logger::Logger( const string& name )
    : ostream( new Private::LoggerBuffer( this ) ),
      d( new Private( this ) )
{
    d->name = name;
    try
    {
        d->logger = alitheia::Logger::_narrow( CorbaHandler::instance()->getObject( "AlitheiaLogger" ) );
    }
    catch( ... )
    {
        cerr << "Got an exception while getting an instance of the Logger. Make sure the Alitheia system is running and eu.sqooss.service.corbaservice is loaded." << endl;
        throw exception();
    }
}

/**
 * Destroys this logger.
 */
Logger::~Logger()
{
    delete rdbuf();
    delete d;
}

/**
 * Sends \a message to the debug channel.
 */
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

/**
 * Sends \a message to the info channel.
 */
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

/**
 * Sends \a message to the warn channel.
 */
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

/**
 * Sends \a message to the error channel.
 */
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

/**
 * Returns this logger's name.
 */
string Logger::name() const
{
    return d->name;
}

/**
 * Set's \a stream as tee stream of the logger.
 * All log messages are even send to \a stream, then.
 */
void Logger::setTeeStream( ostream& stream )
{
    d->copy_stream = &stream;
}
