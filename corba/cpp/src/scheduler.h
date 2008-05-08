#ifndef SCHEDULER_H
#define SCHEDULER_H

#include <string>

namespace Alitheia
{
    class Job;

    class Scheduler
    {
        friend class Job;

    public:
        /**
         * Constructor.
         */
        Scheduler();

        /**
         * Destructor.
         */
        virtual ~Scheduler();

       /** Enqueue \a job.
         * \a job is registered in Alitheia's job scheduler and executed
         * as soon as all dependencies are met.
         */
        void enqueueJob( Job* job );

        bool isExecuting() const;

        void startExecute( int n );

        void stopExecute();

    protected:
        /**
         * Registers \a job in the Alitheia core.
         * The job is executed as possible.
         *
         * \note The job is executed in a different thread.
         */
        int registerJob( Job* job );

        void unregisterJob( const std::string& name );
        void unregisterJob( Job* job );
       
        void addJobDependency( Job* job, Job* dependency );
        void waitForJobFinished( Job* job );

    private:
        class Private;
        Private* d;
    };
}

#endif
