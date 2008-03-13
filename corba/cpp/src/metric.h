#ifndef METRIC_H
#define METRIC_H

#include "Alitheia.h"

namespace Alitheia
{
    class Core;


    struct StoredProject
    {
        StoredProject( const alitheia::StoredProject& project )
            : name( project.name ),
              website( project.website ),
              contact( project.contact ),
              bugs( project.bugs ),
              repository( project.repository ),
              mail( project.mail )
        {
        }

        const std::string name;
        const std::string website;
        const std::string contact;
        const std::string bugs;
        const std::string repository;
        const std::string mail;
    };

    struct ProjectVersion
    {
        ProjectVersion( const alitheia::ProjectVersion& version )
            : project( version.project ),
              version( version.version ),
              timeStamp( version.timeStamp )
        {
        }

        const StoredProject project;
        const int version;
        const int timeStamp;
    };

    struct ProjectFile
    {
        ProjectFile( const alitheia::ProjectFile& file )
            : name( file.name ),
              projectVersion( file.projectVersion ),
              status( file.status )
        {
        }

        const std::string name;
        const ProjectVersion projectVersion;
        const std::string status;
    };


    struct FileGroup
    {
        FileGroup( const alitheia::FileGroup& group )
            : name( group.name ),
              subPath( group.subPath ),
              regex( group.regex ),
              recalcFreq( group.recalcFreq ),
              lastUsed( group.lastUsed ),
              projectVersion( group.projectVersion )
        {
        }

        const std::string name;
        const std::string subPath;
        const std::string regex;
        const int recalcFreq;
        const std::string lastUsed;
        const ProjectVersion projectVersion;
    };
    
    class AbstractMetric : virtual public POA_alitheia::AbstractMetric
    {
        friend class ::Alitheia::Core;
    protected:
        AbstractMetric();
    public:
        ~AbstractMetric();

        virtual char* getAuthor();
        virtual char* getDescription();
        virtual char* getName();
        virtual char* getVersion();
        virtual char* getDateInstalled();

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
        virtual void run( const ProjectFile& projectFile ) const = 0;
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
