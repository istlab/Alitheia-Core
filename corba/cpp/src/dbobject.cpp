#include "dbobject.h"

#include "core.h"

#include "Alitheia.h"

#include "CORBA.h"

using namespace Alitheia;

using std::string;
using std::istream;
using std::streambuf;

StoredProject::StoredProject( const alitheia::StoredProject& project )
    : name( project.name ),
      website( project.website ),
      contact( project.contact ),
      bugs( project.bugs ),
      repository( project.repository ),
      mail( project.mail )
{
}

alitheia::StoredProject StoredProject::toCorba() const
{
    alitheia::StoredProject result;
    result.name = CORBA::string_dup( name.c_str() );
    result.website = CORBA::string_dup( website.c_str() );
    result.contact = CORBA::string_dup( contact.c_str() );
    result.bugs = CORBA::string_dup( bugs.c_str() );
    result.repository = CORBA::string_dup( repository.c_str() );
    return result;
}

ProjectVersion::ProjectVersion( const alitheia::ProjectVersion& version )
    : project( version.project ),
      version( version.version ),
      timeStamp( version.timeStamp )
{
}

alitheia::ProjectVersion ProjectVersion::toCorba() const
{
    alitheia::ProjectVersion result;
    result.project = project.toCorba();
    result.version = version;
    result.timeStamp = timeStamp;
    return result;
}

class ProjectFileBuffer : public streambuf
{
public:
    ProjectFileBuffer( const ProjectFile* file )
        : file( file ),
          buffer( 0 )
    {
    }

    ~ProjectFileBuffer()
    {
        if( buffer != 0 )
            delete[] buffer;
    }

protected:
    int underflow()
    {
        if( buffer == 0 )
        {
            string data = Core::instance()->getFileContents( *file );
            // hm... empty file?
            if( data.size() == 0 )
                return EOF;
            buffer = new char[data.size()];
            memcpy( buffer, data.c_str(), data.size() );
            return buffer[ 0 ];
        }
        else
        {
            return EOF;
        }
    }

private:
    const ProjectFile* const file;
    char* buffer;
};

ProjectFile::ProjectFile( const alitheia::ProjectFile& file )
    : istream( new ProjectFileBuffer( this ) ),
      name( file.name ),
      projectVersion( file.projectVersion ),
      status( file.status )
{
}

ProjectFile::ProjectFile( const ProjectFile& other )
    : istream( new ProjectFileBuffer( this ) ),
      name( other.name ),
      projectVersion( other.projectVersion ),
      status( other.status )
{
}

alitheia::ProjectFile ProjectFile::toCorba() const
{
    alitheia::ProjectFile result;
    result.name = CORBA::string_dup( name.c_str() );
    result.projectVersion = projectVersion.toCorba();
    result.status = CORBA::string_dup( status.c_str() );
    return result;
}

ProjectFile::~ProjectFile()
{
    delete rdbuf();
}

FileGroup::FileGroup( const alitheia::FileGroup& group )
    : name( group.name ),
      subPath( group.subPath ),
      regex( group.regex ),
      recalcFreq( group.recalcFreq ),
      lastUsed( group.lastUsed ),
      projectVersion( group.projectVersion )
{
}
