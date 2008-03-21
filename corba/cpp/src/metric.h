#ifndef METRIC_H
#define METRIC_H

#include "Alitheia.h"

#include "dbobject.h"

namespace Alitheia
{
    class Core;

    class AbstractMetric : virtual public POA_alitheia::AbstractMetric
    {
        friend class ::Alitheia::Core;
    protected:
        AbstractMetric();
    public:
        ~AbstractMetric();

        virtual CORBA::Boolean doInstall();
        virtual CORBA::Boolean doRemove();
        virtual CORBA::Boolean doUpdate();
        virtual char* getAuthor();
        virtual char* getDescription();
        virtual char* getName();
        virtual char* getVersion();
        virtual char* getDateInstalled();

        virtual bool install();
        virtual bool remove();
        virtual bool update();

        virtual std::string author() const = 0;
        virtual std::string description() const = 0;
        virtual std::string name() const = 0;
        virtual std::string version() const = 0;
        virtual std::string dateInstalled() const = 0;

    protected:
        /**
         * @return The name of the metric as it was exported in the ORB.
         * This has nothing to do with getName()
         */
        const std::string& orbName() const;
        /**
         * Sets the name of the object as it is exported in the ORB to \a name.
         * This is set by the core.
         */
        void setOrbName( const std::string& orbName );

        int id() const;
        void setId( int id );

        std::vector<Metric> getSupportedMetrics() const;
        bool addSupportedMetrics( const std::string& description, MetricType::Type type );

    private:
        class Private;
        Private* d;
    };

    class ProjectVersionMetric : public AbstractMetric, virtual public POA_alitheia::ProjectVersionMetric
    {
    public:
        char* getResult( const alitheia::ProjectVersion& projectVersion );
        void run( const alitheia::ProjectVersion& v );
        CORBA::Boolean run2nd( const alitheia::ProjectVersion& a, const alitheia::ProjectVersion& b );

        virtual std::string getResult( const ProjectVersion& projectVersion ) const = 0;
    };

    class ProjectFileMetric : public AbstractMetric, virtual public POA_alitheia::ProjectFileMetric
    {
    public:
        char* getResult( const alitheia::ProjectFile& projectFile );
        void run( const alitheia::ProjectFile& projectFile );

        virtual std::string getResult( const ProjectFile& projectFile ) const = 0;
        virtual void run( ProjectFile& projectFile ) = 0;
    };

    class StoredProjectMetric : public AbstractMetric, virtual public POA_alitheia::StoredProjectMetric
    {
    public:
        char* getResult( const alitheia::StoredProject& storedProject );
        void run( const alitheia::StoredProject& storedProject );

        virtual std::string getResult( const StoredProject& storedProject ) const = 0;
    };

    class FileGroupMetric : public AbstractMetric, virtual public POA_alitheia::FileGroupMetric
    {
    public:
        char* getResult( const alitheia::FileGroup& fileGroup );
        void run( const alitheia::FileGroup& fileGroup );

        virtual std::string getResult( const FileGroup& fileGroup ) const = 0;
    };
}

#endif
