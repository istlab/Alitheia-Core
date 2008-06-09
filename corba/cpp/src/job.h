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
    class Scheduler;

    /**
     * Abstract base class for jobs executable by the Scheduler.
     */
    class Job : virtual public POA_eu::sqooss::impl::service::corba::alitheia::Job
    {
        friend class ::Alitheia::Scheduler;

    public:
        Job();
        ~Job();
        
        enum State
        {
            Created   = ::eu::sqooss::impl::service::corba::alitheia::Job::Created,
            Queued    = ::eu::sqooss::impl::service::corba::alitheia::Job::Queued,
            Running   = ::eu::sqooss::impl::service::corba::alitheia::Job::Running,
            Finished  = ::eu::sqooss::impl::service::corba::alitheia::Job::Finished,
            Error     = ::eu::sqooss::impl::service::corba::alitheia::Job::Error
        };
        
        virtual CORBA::Long priority();
        virtual void run();

        void addDependency( Job* other );
      
        State state() const;

        virtual void stateChanged( State state );

        void waitForFinished();

        static void* operator new( size_t s );
        static void operator delete( void* o );

    protected:
        void setState( eu::sqooss::impl::service::corba::alitheia::Job::JobState state );
        
        const std::string& name() const;
        void setName( const std::string& name );

        int id() const;
        void setId( int id );

    private:
        class Private;
        Private* d;
    };
};

std::ostream& operator<<( std::ostream& stream, Alitheia::Job::State state );

#endif
