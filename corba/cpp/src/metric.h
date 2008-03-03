#ifndef METRIC_H
#define METRIC_H

#include "Alitheia.h"

namespace Alitheia
{
    class Core;

    class Metric : virtual public POA_alitheia::Metric
    {
        friend class ::Alitheia::Core;
    protected:
        Metric();
    public:
        ~Metric();

        virtual char* getAuthor();
        virtual char* getDescription();
        virtual char* getName();
        virtual char* getVersion();
        virtual char* getResult();
        virtual char* getDateInstalled();

    protected:
        const std::string& name() const;
        void setName( const std::string& name );

        int id() const;
        void setId( int id );

    private:
        class Private;
        Private* d;
    };
}

#endif
