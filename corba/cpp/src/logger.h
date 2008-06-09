#ifndef LOGGER_H
#define LOGGER_H

#include <string>
#include <ostream>

#include "Alitheia.h"

namespace Alitheia
{
    /**
     * @brief Interface class to the Alitheia logging system via CORBA.
     *
     * Use a Logger object with one of the predefined logger names to
     * log messages to Alitheia.
     *
     * Logger can be used of std::ostream, therefore all the convenient 
     * streaming operators can be used. If you do this, they're logged as info.
     */
    class Logger : public std::ostream
    {
    public:
        static const std::string NameSqoOss;
        static const std::string NameSqoOssService;
        static const std::string NameSqoOssDatabase;
        static const std::string NameSqoOssSecurity;
        static const std::string NameSqoOssMessaging;
        static const std::string NameSqoOssWebServices;
        static const std::string NameSqoOssScheduling;
        static const std::string NameSqoOssUpdater;
        static const std::string NameSqoOssWebAdmin;
        static const std::string NameSqoOssTDS;
        static const std::string NameSqoOssFDS;
        static const std::string NameSqoOssMetric;
        static const std::string NameSqoOssTester;

        explicit Logger( const std::string& name = NameSqoOss );
        ~Logger();

        void debug( const std::string& message );
        void info( const std::string& message );
        void warn( const std::string& message );
        void error( const std::string& message );

        std::string name() const;

        void setTeeStream( std::ostream& stream );

    private:
        class Private;
        Private* d;
    };
}

#endif
