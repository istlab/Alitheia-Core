#ifndef JOB_H
#define JOB_H

#include "Alitheia.h"

namespace Alitheia
{
    class Job : virtual public POA_alitheia::Job
    {
        virtual CORBA::Long priority();
        virtual void run();
    };
};

#endif
