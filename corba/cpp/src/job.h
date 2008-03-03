#ifndef JOB_H
#define JOB_H

#include "Alitheia.h"

namespace std
{
    typedef basic_string< char > string;
}

namespace Alitheia
{
    class Core;

    class Job : virtual public POA_alitheia::Job
    {
        friend class ::Alitheia::Core;
    public:
        Job();
        ~Job();
        
        enum State
        {
            Created   = ::alitheia::Job::Created,
            Queued    = ::alitheia::Job::Queued,
            Running   = ::alitheia::Job::Running,
            Finished  = ::alitheia::Job::Finished,
            Error     = ::alitheia::Job::Error
        };
        
        virtual CORBA::Long priority();
        virtual void run();

        void addDependency( Job* other );
       
        State state() const;

        virtual void stateChanged( State state );

        void waitForFinished();

    protected:
        void setState( alitheia::Job::JobState state );
        
        const std::string& name() const;
        void setName( const std::string& name );
        
    private:
        class Private;
        Private* d;
    };
};

std::ostream& operator<<( std::ostream& stream, Alitheia::Job::State state );

#endif
