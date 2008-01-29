#ifndef CORE_H
#define CORE_H

#include "Alitheia.h"

namespace std
{
    typedef basic_string< char >    string;
}

namespace Alitheia
{
    class Job;
    class Metric;

    /**
     * @brief The main connection to register metrics in the Alitheia system.
     *
     * Core is the central connection into the Alitheia core system. You can
     * use Core to register and unregister metrics and to run a local ORB.
     */
    class Core
    {
        friend class Job;
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
         * Registers \a metric in the Alitheia core by using \a name
         * as it's name in the ORB.
         * @return The ID assigned by Alitheia
         */
        int registerMetric( const std::string& name, Metric* metric );
        /**
         * Unregisters a metric with \a id from the Alitheia core. The ID
         * is the one returned by registerMetric.
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

        /** Enqueue \a job.
         * \a job is registered in Alitheia's job scheduler and executed
         * as soon as all dependencies are met.
         */
        void enqueueJob( Job* job );
   
    protected:
        void addJobDependency( Job* job, Job* dependency );
        void waitForJobFinished( Job* job );
        
    private:
        class Private;
        Private* d;
    };
}

#endif
