#ifndef SCHEDULER_H
#define SCHEDULER_H

#include <string>

namespace Alitheia
{
    class Job;

    /**
     * The class Scheduler can be used to let jobs being executed by 
     * Alitheia's job scheduler.
     */
    class Scheduler
    {
        friend class Job;

    public:
        Scheduler();
        virtual ~Scheduler();

        void enqueueJob( Job* job );

        bool isExecuting() const;

        void startExecute( int n );

        void stopExecute();

    protected:
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
