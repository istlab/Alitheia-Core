#ifndef METRIC_H
#define METRIC_H

#include "Alitheia.h"

namespace Alitheia
{
    class Metric : virtual public POA_alitheia::Metric
    {
    public:
        virtual char* getAuthor();
        virtual char* getDescription();
        virtual char* getName();
        virtual char* getVersion();
        virtual char* getResult();
        virtual char* getDateInstalled();
    };
}

#endif
