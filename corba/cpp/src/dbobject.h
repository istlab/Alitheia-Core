#ifndef DBOBJECT_H
#define DBOBJECT_H

#include <istream>
#include <string>    

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

    class StoredProject : public DAObject
    {
    public:
        StoredProject() {}
        explicit StoredProject( const alitheia::StoredProject& project );

        alitheia::StoredProject toCorba() const;
        operator CORBA::Any() const;
       
        std::string name;
        std::string website;
        std::string contact;
        std::string bugs;
        std::string repository;
        std::string mail;
    };

    class ProjectVersion : public DAObject
    {
    public:
        ProjectVersion() : version(0), timeStamp(0){}
        explicit ProjectVersion( const alitheia::ProjectVersion& version );
        
        alitheia::ProjectVersion toCorba() const;
        operator CORBA::Any() const;

        StoredProject project;
        int version;
        int timeStamp;
    };

    class ProjectFile : public std::istream, public DAObject
    {
    public:
        ProjectFile();
        explicit ProjectFile( const alitheia::ProjectFile& file );
        explicit ProjectFile( const ProjectFile& other );
        ~ProjectFile();

        alitheia::ProjectFile toCorba() const;
        operator CORBA::Any() const;

        ProjectFile& operator=( const ProjectFile& other );

        std::string name;
        ProjectVersion projectVersion;
        std::string status;
    };


    class FileGroup : public DAObject
    {
    public:
        explicit FileGroup( const alitheia::FileGroup& group );

        alitheia::FileGroup toCorba() const;
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
            SourceCode = ::alitheia::SourceCode,
            MailingList = ::alitheia::MailingList,
            BugDatabase = ::alitheia::BugDatabase
        };

        MetricType() : type( SourceCode ) {}
        explicit MetricType( const alitheia::MetricType& metrictype );

        alitheia::MetricType toCorba() const;
        operator CORBA::Any() const;

        Type type;
    };

    class Plugin : public DAObject
    {
    public:
        Plugin() {}
        explicit Plugin( const alitheia::Plugin& plugin );

        alitheia::Plugin toCorba() const;
        operator CORBA::Any() const;

        std::string name;
        std::string installdate;
    };

    class Metric : public DAObject
    {
    public:
        Metric() {}
        explicit Metric( const alitheia::Metric& metric );

        alitheia::Metric toCorba() const;
        operator CORBA::Any() const;

        Plugin plugin;
        MetricType metricType;
        std::string description;
    };

    class ProjectFileMeasurement : public DAObject
    {
    public:
        ProjectFileMeasurement() {}
        explicit ProjectFileMeasurement( const alitheia::ProjectFileMeasurement& measurement );

        alitheia::ProjectFileMeasurement toCorba() const;
        operator CORBA::Any() const;

        Metric metric;
        ProjectFile projectFile;
        std::string whenRun;
        std::string result;
    };
}

#endif
