#ifndef CORE_H
#define CORE_H

#include <string>

#include "dbobject.h"

namespace Alitheia
{
    class Job;
    class AbstractMetric;
    class ProjectVersion;
    class ProjectFileBuffer;
    class Scheduler;

    /**
     * @brief The main connection to register metrics in the Alitheia system.
     *
     * Core is the central connection into the Alitheia core system. You can
     * use Core to register and unregister metrics and to run a local ORB.
     */
    class Core
    {
        friend class ::Alitheia::AbstractMetric;
        friend class ::Alitheia::ProjectVersion;
        friend class ::Alitheia::ProjectFileBuffer;
        friend class ::Alitheia::Scheduler;

    protected:
        Core();

    public:
        virtual ~Core();

        static Core* instance();
        
        int registerMetric( AbstractMetric* metric );
        void unregisterMetric( AbstractMetric* metric );
        void unregisterMetric( int id );

        void run();
        void shutdown();

    protected:
        bool addSupportedMetrics( AbstractMetric* metric, const std::string& description, 
                                  const std::string& mnemonic, MetricType::Type type ) const;
        std::vector< Metric > getSupportedMetrics( const AbstractMetric* metric ) const;
        std::vector< ProjectFile > getVersionFiles( const ProjectVersion& version ) const;

        int getUniqueId() const;

    private:
        class Private;
        Private* d;
    };
}

#endif
