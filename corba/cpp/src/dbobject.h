#ifndef DBOBJECT_H
#define DBOBJECT

#include <istream>
#include <string>    

/*namespace alitheia
{
    class StoredProject;
    class ProjectVersion;
    class ProjectFile;
    class FileGroup;
}*/

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

        const int id;
    };

    class StoredProject : public DAObject
    {
    public:
        StoredProject() {}
        explicit StoredProject( const alitheia::StoredProject& project );

        alitheia::StoredProject toCorba() const;
        operator CORBA::Any() const;
       
        const std::string name;
        const std::string website;
        const std::string contact;
        const std::string bugs;
        const std::string repository;
        const std::string mail;
    };

    class ProjectVersion : public DAObject
    {
    public:
        ProjectVersion() : version(0), timeStamp(0){}
        explicit ProjectVersion( const alitheia::ProjectVersion& version );
        
        alitheia::ProjectVersion toCorba() const;
        operator CORBA::Any() const;

        const StoredProject project;
        const int version;
        const int timeStamp;
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

        const std::string name;
        const ProjectVersion projectVersion;
        const std::string status;
    };


    class FileGroup : public DAObject
    {
    public:
        explicit FileGroup( const alitheia::FileGroup& group );

        alitheia::FileGroup toCorba() const;
        operator CORBA::Any() const;

        const std::string name;
        const std::string subPath;
        const std::string regex;
        const int recalcFreq;
        const std::string lastUsed;
        const ProjectVersion projectVersion;
    };
}

#endif
