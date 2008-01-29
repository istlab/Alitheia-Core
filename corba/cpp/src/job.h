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
        friend class Core;
    public:
        Job();
        ~Job();
        
        typedef ::alitheia::Job::JobState State;
        
        virtual CORBA::Long priority();
        virtual void run();

        void addDependency( Job* other );
       
        State state() const;

        virtual void stateChanged( State state );

        void waitForFinished();

    protected:
        void setState( State state );
        
        const std::string& name() const;
        void setName( const std::string& name );
        
    private:
        class Private;
        Private* d;
    };
};

std::ostream& operator<<( std::ostream& stream, Alitheia::Job::State state );

#endif
