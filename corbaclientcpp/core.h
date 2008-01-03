#ifndef CORE_H
#define CORE_H

#include <string>

#include "Alitheia.h"

namespace Alitheia
{
    class Metric;

    class Core
    {
    public:
        Core();
        virtual ~Core();

        int registerMetric( const std::string& name, Metric* metric );
        void unregisterMetric( int id );

        void run();

    private:
        alitheia::Core_var m_core;
        std::map< int, std::string > m_registeredServices;
    };
}

#endif
