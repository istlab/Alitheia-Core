#ifndef CORE_H
#define CORE_H

#include <string>

#include "Alitheia.h"

namespace Alitheia
{
    class Core
    {
    public:
        Core();
        virtual ~Core();

        int registerMetric( const std::string& name, const alitheia::Metric_var& metric );
        void unregisterMetric( int id );

    private:
        alitheia::Core_var m_core;
        std::map< int, std::string > m_registeredServices;
    };
}

#endif
