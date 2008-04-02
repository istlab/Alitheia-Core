#ifndef CORE_H
#define CORE_H

#include <string>

#include "dbobject.h"

namespace Alitheia
{
    class Job;
    class AbstractMetric;
    class ProjectVersion;
    class ProjectFileBuffe;

    /**
     * @brief The main connection to register metrics in the Alitheia system.
     *
     * Core is the central connection into the Alitheia core system. You can
     * use Core to register and unregister metrics and to run a local ORB.
     */
    class Core
    {
        friend class AbstractMetric;
        friend class Job;
        friend class ProjectVersion;
        friend class ProjectFileBuffer;

    protected:
        /**
         * Constructor.
         */
        Core();

    public:
        /**
         * Destructor.
         */
        virtual ~Core();

        /**
         * Get a singleton instance.
         */
        static Core* instance();
        
        /**
         * Registers \a metric in the Alitheia core.
         * @return The ID assigned by Alitheia
         */
        int registerMetric( AbstractMetric* metric );
        /**
         * Unregisters \a metric from the Alitheia core.
         */
        void unregisterMetric( AbstractMetric* metric );
        /**
         * Unregisters the metric with \a id from the Alitheia core.
         */
        void unregisterMetric( int id );

        /**
         * Registers \a job in the Alitheia core.
         * The job is executed as possible.
         *
         * \note The job is executed in a different thread.
         */
        int registerJob( Job* job );
       
        /**
         * Runs the local ORB.
         * You need to call run after registered metrics. Otherwise it would
         * not be possible to call their methods.
         *
         * This method is blocking as long as the ORB is running.
         */
        void run();

        /**
         * Shut down the core.
         * This method is unregistering all registered objects and then stopping
         * the ORB.
         */
        void shutdown();

        /** Enqueue \a job.
         * \a job is registered in Alitheia's job scheduler and executed
         * as soon as all dependencies are met.
         */
        void enqueueJob( Job* job );
        
    protected:
        void addJobDependency( Job* job, Job* dependency );
        void waitForJobFinished( Job* job );
        
        std::string getFileContents( const ProjectFile& file );

        bool addSupportedMetrics( AbstractMetric* metric, const std::string& description, MetricType::Type type ) const;
        std::vector< Metric > getSupportedMetrics( const AbstractMetric* metric ) const;
        std::vector< ProjectFile > getVersionFiles( const ProjectVersion& version ) const;

    private:
        class Private;
        Private* d;
    };
}

#endif
