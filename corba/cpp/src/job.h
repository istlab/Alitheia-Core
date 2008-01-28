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
        
        virtual CORBA::Long priority();
        virtual void run();
    
        void addDependency( Job* other );
        
    protected:
        const std::string& name() const;
        void setName( const std::string& name );
        
    private:
        class Private;
        Private* d;
    };
};

#endif
