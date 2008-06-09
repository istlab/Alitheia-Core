#ifndef METRIC_H
#define METRIC_H

#include "Alitheia.h"

#include "dbobject.h"

namespace Alitheia
{
    class Core;

    /**
     * Abstract base class used for all metrics.
     */
    class AbstractMetric : virtual public POA_eu::sqooss::impl::service::corba::alitheia::AbstractMetric
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
        virtual std::string dateInstalled() const;

    protected:
        const std::string& orbName() const;
        void setOrbName( const std::string& orbName );

        int id() const;
        void setId( int id );

    public:
        std::vector<Metric> getSupportedMetrics() const;

    protected:
        bool addSupportedMetrics( const std::string& description, const std::string& mnemonic, MetricType::Type type );

    private:
        class Private;
        Private* d;
    };

    /**
     * Base class for ProjectVersion metrics.
     * Reimplement this class and register the instance in 
     * the Core to let Alitheia execute your metric.
     */
    class ProjectVersionMetric : public AbstractMetric, virtual public POA_eu::sqooss::impl::service::corba::alitheia::ProjectVersionMetric
    {
    public:
        char* doGetResult( const eu::sqooss::impl::service::corba::alitheia::ProjectVersion& projectVersion );
        void doRun( const eu::sqooss::impl::service::corba::alitheia::ProjectVersion& v );

        virtual std::string getResult( const ProjectVersion& projectVersion ) const = 0;
        virtual void run( ProjectVersion& version ) = 0;
    };

    /**
     * Base class for ProjectFile metrics.
     * Reimplement this class and register the instance in 
     * the Core to let Alitheia execute your metric.
     */
    class ProjectFileMetric : public AbstractMetric, virtual public POA_eu::sqooss::impl::service::corba::alitheia::ProjectFileMetric
    {
    public:
        char* doGetResult( const eu::sqooss::impl::service::corba::alitheia::ProjectFile& projectFile );
        void doRun( const eu::sqooss::impl::service::corba::alitheia::ProjectFile& projectFile );

        virtual std::string getResult( const ProjectFile& projectFile ) const = 0;
        virtual void run( ProjectFile& projectFile ) = 0;
    };

    /**
     * Base class for StoredProject metrics.
     * Reimplement this class and register the instance in 
     * the Core to let Alitheia execute your metric.
     */
    class StoredProjectMetric : public AbstractMetric, virtual public POA_eu::sqooss::impl::service::corba::alitheia::StoredProjectMetric
    {
    public:
        char* doGetResult( const eu::sqooss::impl::service::corba::alitheia::StoredProject& storedProject );
        void doRun( const eu::sqooss::impl::service::corba::alitheia::StoredProject& storedProject );

        virtual std::string getResult( const StoredProject& storedProject ) const = 0;
    };

    /**
     * Base class for FileGroup metrics.
     * Reimplement this class and register the instance in 
     * the Core to let Alitheia execute your metric.
     */
    class FileGroupMetric : public AbstractMetric, virtual public POA_eu::sqooss::impl::service::corba::alitheia::FileGroupMetric
    {
    public:
        char* doGetResult( const eu::sqooss::impl::service::corba::alitheia::FileGroup& fileGroup );
        void doRun( const eu::sqooss::impl::service::corba::alitheia::FileGroup& fileGroup );

        virtual std::string getResult( const FileGroup& fileGroup ) const = 0;
    };
}

#endif
