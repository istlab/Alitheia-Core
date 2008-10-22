#ifndef DBOBJECT_H
#define DBOBJECT_H

#include <iostream>
#include <string>    
#include <vector>

#include <boost/date_time/posix_time/posix_time_types.hpp>

#include "Alitheia.h"

namespace Alitheia
{
    class DAObject
    {
    protected:
        DAObject() : id( 0 ) {}
        DAObject( int id );

    public:
        virtual ~DAObject();

        virtual operator CORBA::Any() const = 0;

        int id;
    };

    class Bug;
    class ProjectVersion;

    class StoredProject : public DAObject
    {
    public:
        StoredProject() {}
        explicit StoredProject( const eu::sqooss::impl::service::corba::alitheia::StoredProject& project );
        static StoredProject fromCorba( const CORBA::Any& project );

        eu::sqooss::impl::service::corba::alitheia::StoredProject toCorba() const;
        operator CORBA::Any() const;
 
        static StoredProject getProjectByName( const std::string& name );
        static ProjectVersion getLastProjectVersion( const StoredProject& project );
     
        std::vector< Bug > getBugs() const;

        std::string name;
        std::string website;
        std::string contact;
        std::string repository;
        std::string mail;
    };

    class Developer : public DAObject
    {
    public:
        Developer() {}
        explicit Developer( const eu::sqooss::impl::service::corba::alitheia::Developer& developer );
        static Developer fromCorba( const CORBA::Any& developer );

        eu::sqooss::impl::service::corba::alitheia::Developer toCorba() const;
        operator CORBA::Any() const;

        static Developer byEmail( const std::string& email, const StoredProject& sp );
        static Developer byUsername( const std::string& email, const StoredProject& sp );

        std::string name;
        std::string email;
        std::string username;
        StoredProject storedProject;
    };

    class BugResolution : public DAObject
    {
    public:
        BugResolution() {}
        explicit BugResolution( const eu::sqooss::impl::service::corba::alitheia::BugResolution& resolution );
        static BugResolution fromCorba( const CORBA::Any& resolution );

        eu::sqooss::impl::service::corba::alitheia::BugResolution toCorba() const;
        operator CORBA::Any() const;

        std::string resolution;
    };

    class BugPriority : public DAObject
    {
    public:
        BugPriority() {}
        explicit BugPriority( const eu::sqooss::impl::service::corba::alitheia::BugPriority& priority );
        static BugPriority fromCorba( const CORBA::Any& priority );

        eu::sqooss::impl::service::corba::alitheia::BugPriority toCorba() const;
        operator CORBA::Any() const;

        std::string priority;
    };

    class BugSeverity : public DAObject
    {
    public:
        BugSeverity() {}
        explicit BugSeverity( const eu::sqooss::impl::service::corba::alitheia::BugSeverity& severity );
        static BugSeverity fromCorba( const CORBA::Any& severity );

        eu::sqooss::impl::service::corba::alitheia::BugSeverity toCorba() const;
        operator CORBA::Any() const;

        std::string severity;
    };

    class Bug : public DAObject
    {
    public:
        Bug() {}
        explicit Bug( const eu::sqooss::impl::service::corba::alitheia::Bug& bug );
        static Bug fromCorba( const CORBA::Any& severity );

        eu::sqooss::impl::service::corba::alitheia::Bug toCorba() const;
        operator CORBA::Any() const;

        StoredProject project;
        boost::posix_time::ptime updateRun;
        std::string bugId;
        boost::posix_time::ptime creationTS;
        boost::posix_time::ptime deltaTS;
        Developer reporter;
        BugResolution resolution;
        BugPriority priority;
        BugSeverity severity;
        std::string shortDesc;
    };

    class Directory : public DAObject
    {
    public:
        Directory() {}
        explicit Directory( const eu::sqooss::impl::service::corba::alitheia::Directory& directory );
        static Directory fromCorba( const CORBA::Any& directory );

        Directory getDirectory( const std::string& path );

        eu::sqooss::impl::service::corba::alitheia::Directory toCorba() const;
        operator CORBA::Any() const;

        std::string path;
    };

    class ProjectFile;
    class ProjectVersion : public DAObject
    {
    public:
        ProjectVersion() : timeStamp(0){}
        explicit ProjectVersion( const eu::sqooss::impl::service::corba::alitheia::ProjectVersion& version );
        static ProjectVersion fromCorba( const CORBA::Any& version );
        
        eu::sqooss::impl::service::corba::alitheia::ProjectVersion toCorba() const;
        operator CORBA::Any() const;

        std::vector< ProjectFile > getVersionFiles() const;

        StoredProject project;
        std::string version;
        int timeStamp;
        Developer committer;
        std::string commitMsg;
        std::string properties;
    };

    class ProjectFile : public std::istream, public DAObject
    {
    public:
        ProjectFile();
        explicit ProjectFile( const eu::sqooss::impl::service::corba::alitheia::ProjectFile& file );
        static ProjectFile fromCorba( const CORBA::Any& file );
        ProjectFile( const ProjectFile& other );
        ~ProjectFile();

        eu::sqooss::impl::service::corba::alitheia::ProjectFile toCorba() const;
        operator CORBA::Any() const;

        ProjectFile& operator=( const ProjectFile& other );

        void save( std::ostream& stream ) const;
        void save( const std::string& filename ) const;

        std::string getFileName() const;

        std::string name;
        ProjectVersion projectVersion;
        std::string status;
        bool isDirectory;
        Directory directory;
    };

    class FileGroup : public DAObject
    {
    public:
        explicit FileGroup( const eu::sqooss::impl::service::corba::alitheia::FileGroup& group );
        static FileGroup fromCorba( const CORBA::Any& group );

        eu::sqooss::impl::service::corba::alitheia::FileGroup toCorba() const;
        operator CORBA::Any() const;

        std::string name;
        std::string subPath;
        std::string regex;
        int recalcFreq;
        std::string lastUsed;
        ProjectVersion projectVersion;
    };

    class MetricType : public DAObject
    {
    public:
        enum Type
        {
            SourceCode = ::eu::sqooss::impl::service::corba::alitheia::SourceCode,
            MailingList = ::eu::sqooss::impl::service::corba::alitheia::MailingList,
            BugDatabase = ::eu::sqooss::impl::service::corba::alitheia::BugDatabase,
            ProjectWide = ::eu::sqooss::impl::service::corba::alitheia::ProjectWide
        };

        MetricType() : type( SourceCode ) {}
        explicit MetricType( const eu::sqooss::impl::service::corba::alitheia::MetricType& metrictype );
        static MetricType fromCorba( const CORBA::Any& metrictype );

        static MetricType getMetricType( Type t );

        eu::sqooss::impl::service::corba::alitheia::MetricType toCorba() const;
        operator CORBA::Any() const;

        Type type;
    };

    class Plugin : public DAObject
    {
    public:
        Plugin() {}
        explicit Plugin( const eu::sqooss::impl::service::corba::alitheia::Plugin& plugin );
        static Plugin fromCorba( const CORBA::Any& plugin );

        eu::sqooss::impl::service::corba::alitheia::Plugin toCorba() const;
        operator CORBA::Any() const;

        std::string name;
        std::string installdate;
    };

    class PluginConfiguration : public DAObject
    {
    public:
        PluginConfiguration() {}
        explicit PluginConfiguration( const eu::sqooss::impl::service::corba::alitheia::PluginConfiguration& config );
        static PluginConfiguration fromCorba( const CORBA::Any& config );

        eu::sqooss::impl::service::corba::alitheia::PluginConfiguration toCorba() const;
        operator CORBA::Any() const;

        std::string name;
        std::string value;
        std::string type;
        std::string msg;
        Plugin plugin;
    };

    class Metric : public DAObject
    {
    public:
        Metric() {}
        explicit Metric( const eu::sqooss::impl::service::corba::alitheia::Metric& metric );
        static Metric fromCorba( const CORBA::Any& metric );

        eu::sqooss::impl::service::corba::alitheia::Metric toCorba() const;
        operator CORBA::Any() const;

        Plugin plugin;
        MetricType metricType;
        std::string mnemonic;
        std::string description;
    };

    class ProjectFileMeasurement : public DAObject
    {
    public:
        ProjectFileMeasurement() {}
        explicit ProjectFileMeasurement( const eu::sqooss::impl::service::corba::alitheia::ProjectFileMeasurement& measurement );
        static ProjectFileMeasurement fromCorba( const CORBA::Any& measurement );

        eu::sqooss::impl::service::corba::alitheia::ProjectFileMeasurement toCorba() const;
        operator CORBA::Any() const;

        Metric metric;
        ProjectFile projectFile;
        std::string result;
    };

    class ProjectVersionMeasurement : public DAObject
    {
    public:
        ProjectVersionMeasurement() {}
        explicit ProjectVersionMeasurement( const eu::sqooss::impl::service::corba::alitheia::ProjectVersionMeasurement& measurement );
        static ProjectVersionMeasurement fromCorba( const CORBA::Any& measurement );

        eu::sqooss::impl::service::corba::alitheia::ProjectVersionMeasurement toCorba() const;
        operator CORBA::Any() const;

        Metric metric;
        ProjectVersion projectVersion;
        std::string result;
    };
}

#endif
