#include <exception>

#include "logger.h"
#include "corbahandler.h"

using namespace std;
using namespace Alitheia;

const std::string Logger::NameSqoOss            = "sqooss";
const std::string Logger::NameSqoOssService     = "sqooss.service";
const std::string Logger::NameSqoOssDatabase    = "sqooss.database";
const std::string Logger::NameSqoOssSecurity    = "sqooss.security";
const std::string Logger::NameSqoOssMessaging   = "sqooss.messaging";
const std::string Logger::NameSqoOssWebServices = "sqooss.messaging";
const std::string Logger::NameSqoOssScheduling  = "sqooss.scheduler";
const std::string Logger::NameSqoOssUpdater     = "sqooss.scheduler";
const std::string Logger::NameSqoOssWebAdmin    = "sqooss.webadmin";
const std::string Logger::NameSqoOssTDS         = "sqooss.tds";
const std::string Logger::NameSqoOssFDS         = "sqooss.tds";
const std::string Logger::NameSqoOssMetric      = "sqooss.metric";
const std::string Logger::NameSqoOssTester      = "sqooss.tester";

Logger::Logger( const string& name )
    : m_name( name )
{
    m_logger = alitheia::Logger::_narrow( CorbaHandler::instance()->getObject( "Logger" ) );
}

Logger::~Logger()
{
}

void Logger::debug( const std::string& message )
{
    m_logger->debug( m_name.c_str(), message.c_str() );
}

void Logger::info( const std::string& message )
{
    m_logger->info( m_name.c_str(), message.c_str() );
}

void Logger::warn( const std::string& message )
{
    m_logger->warn( m_name.c_str(), message.c_str() );
}

void Logger::error( const std::string& message )
{
    m_logger->error( m_name.c_str(), message.c_str() );
}

string Logger::name() const
{
    return m_name;
}
