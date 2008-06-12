#ifndef METRIC_H
#define METRIC_H

#include "Alitheia.h"

#include "dbobject.h"

#include <boost/variant.hpp>

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

    class ResultEntry
    {
    public:
        typedef boost::variant< int, 
                                long, 
                                double, 
                                float, 
                                std::string, 
                                std::vector< char > > value_type;
        
        ResultEntry() {}
        ResultEntry( const value_type& value, const std::string& mimeType, const std::string& mnemonic );
        //explicit ResultEntry( const eu::sqooss::impl::service::corba::alitheia::ResultEntry& entry );
        virtual ~ResultEntry();
    
        static const std::string MimeTypeTypeInteger;
        static const std::string MimeTypeTypeLong;
        static const std::string MimeTypeTypeFloat;
        static const std::string MimeTypeTypeDouble;
        static const std::string MimeTypeTextPlain;
        static const std::string MimeTypeTextHtml;
        static const std::string MimeTypeTextCsv;
        static const std::string MimeTypeImageGif;
        static const std::string MimeTypeImagePng;
        static const std::string MimeTypeImageJpeg;
        
        eu::sqooss::impl::service::corba::alitheia::ResultEntry toCorba() const;
    
        value_type value;
        std::string mimeType;
        std::string mnemonic;
    };
    
    /**
     * Base class for ProjectVersion metrics.
     * Reimplement this class and register the instance in 
     * the Core to let Alitheia execute your metric.
     */
    class ProjectVersionMetric : public AbstractMetric, virtual public POA_eu::sqooss::impl::service::corba::alitheia::ProjectVersionMetric
    {
    public:
        eu::sqooss::impl::service::corba::alitheia::Result*
              doGetResult( const eu::sqooss::impl::service::corba::alitheia::ProjectVersion& projectVersion,
                           const eu::sqooss::impl::service::corba::alitheia::Metric& metric );
        void doRun( const eu::sqooss::impl::service::corba::alitheia::ProjectVersion& v );

        virtual std::vector< ResultEntry > getResult( const ProjectVersion& projectVersion, const Metric& m ) const = 0;
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
        eu::sqooss::impl::service::corba::alitheia::Result*
              doGetResult( const eu::sqooss::impl::service::corba::alitheia::ProjectFile& projectFile,
                           const eu::sqooss::impl::service::corba::alitheia::Metric& metric );
        void doRun( const eu::sqooss::impl::service::corba::alitheia::ProjectFile& projectFile );

        virtual std::vector< ResultEntry > getResult( const ProjectFile& projectFile, const Metric& m ) const = 0;
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
        eu::sqooss::impl::service::corba::alitheia::Result*
              doGetResult( const eu::sqooss::impl::service::corba::alitheia::StoredProject& storedProject,
                           const eu::sqooss::impl::service::corba::alitheia::Metric& metric );
        void doRun( const eu::sqooss::impl::service::corba::alitheia::StoredProject& storedProject );

        virtual std::vector< ResultEntry > getResult( const StoredProject& storedProject, const Metric& m ) const = 0;
        virtual void run( StoredProject& projectFile ) = 0;
    };

    /**
     * Base class for FileGroup metrics.
     * Reimplement this class and register the instance in 
     * the Core to let Alitheia execute your metric.
     */
    class FileGroupMetric : public AbstractMetric, virtual public POA_eu::sqooss::impl::service::corba::alitheia::FileGroupMetric
    {
    public:
        eu::sqooss::impl::service::corba::alitheia::Result*
              doGetResult( const eu::sqooss::impl::service::corba::alitheia::FileGroup& fileGroup,
                           const eu::sqooss::impl::service::corba::alitheia::Metric& metric );
        void doRun( const eu::sqooss::impl::service::corba::alitheia::FileGroup& fileGroup );

        virtual std::vector< ResultEntry > getResult( const FileGroup& fileGroup, const Metric& m ) const = 0;
        virtual void run( FileGroup& projectFile ) = 0;
    };
}

#endif
