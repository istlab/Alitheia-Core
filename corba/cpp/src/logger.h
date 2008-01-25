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

        /**
         * Constructor for a Logger with name \a name.
         */
        explicit Logger( const std::string& name = NameSqoOss );
        /**
         * Destructor.
         */
        ~Logger();

        /**
         * Writes \a message to the debug logger.
         */
        void debug( const std::string& message );
        /**
         * Writes \a message to the info logger.
         */
        void info( const std::string& message );
        /**
         * Writes \a message to the warn logger.
         */
        void warn( const std::string& message );
        /**
         * Writes \a message to the error logger.
         */
        void error( const std::string& message );

        /**
         * @return The name of the logger.
         */
        std::string name() const;

        /**
         * Sets \a stream as a tee-stream for output.
         *
         * Use setTeeStream( std::cout ) to have all logged
         * data copied to the standard output.
         */
        void setTeeStream( std::ostream& stream );

    private:
        class Private;
        Private* d;
    };
}

#endif
