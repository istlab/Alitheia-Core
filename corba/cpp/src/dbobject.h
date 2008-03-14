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
    class StoredProject
    {
    public:
        StoredProject() : id(0){}
        explicit StoredProject( const alitheia::StoredProject& project );

        alitheia::StoredProject toCorba() const;
       
        const int id;
        const std::string name;
        const std::string website;
        const std::string contact;
        const std::string bugs;
        const std::string repository;
        const std::string mail;
    };

    class ProjectVersion
    {
    public:
        ProjectVersion() : id(0), version(0), timeStamp(0){}
        explicit ProjectVersion( const alitheia::ProjectVersion& version );
        
        alitheia::ProjectVersion toCorba() const;

        const int id;
        const StoredProject project;
        const int version;
        const int timeStamp;
    };

    class ProjectFile : public std::istream
    {
    public:
        ProjectFile();
        explicit ProjectFile( const alitheia::ProjectFile& file );
        explicit ProjectFile( const ProjectFile& other );
        ~ProjectFile();

        alitheia::ProjectFile toCorba() const;

        const int id;
        const std::string name;
        const ProjectVersion projectVersion;
        const std::string status;
    };


    class FileGroup
    {
    public:
        explicit FileGroup( const alitheia::FileGroup& group );

        const int id;
        const std::string name;
        const std::string subPath;
        const std::string regex;
        const int recalcFreq;
        const std::string lastUsed;
        const ProjectVersion projectVersion;
    };
}

#endif
