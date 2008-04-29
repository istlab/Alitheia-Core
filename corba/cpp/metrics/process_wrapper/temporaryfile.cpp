#include "temporaryfile.h"

#include <fstream>
#include <boost/filesystem/operations.hpp>

using std::string;
using std::ios_base;
using std::iostream;
using std::filebuf;

/**
 * Constructs a TemporaryDirectory with a template filename of \a templateName.
 * This will be used to create a unique filename. If the \a templateName does 
 * not contain XXXXXX it will automatically be appended and used as the
 * dynamic portion of the filename.
 */
TemporaryDirectory::TemporaryDirectory( const char* templateName )
{
    string name = templateName;
    if( name.find( "XXXXXX" ) == string::npos )
    {
        name += "XXXXXX";
    }
    m_name = strdup( name.c_str() );
    mkdtemp( m_name );
}

/**
 * Helper function deleting a directory.
 */
/*void deleteDir( const char* name )
{
    if( unlink( name ) == -1 && errno == EISDIR )
    {
        chdir( name );
        
    }
}*/

/**
 * Destroys the temporary directory object. The directory is deleted.
 */
TemporaryDirectory::~TemporaryDirectory()
{
    boost::filesystem::remove_all( m_name );
    delete m_name;
}

/**
 * Returns the complete unique directory name.
 */
string TemporaryDirectory::name() const
{
    return m_name;
}

/**
 * Constructs a TemporaryFile with a template filename of \a templateName.
 * This will be used to create a unique filename. If the \a templateName does 
 * not contain XXXXXX it will automatically be appended and used as the
 * dynamic portion of the filename.
 * The file gets opened with \a mode.
 */
TemporaryFile::TemporaryFile( const char* templateName, ios_base::openmode mode )
    : iostream( new filebuf() )
{
    string name = templateName;
    if( name.find( "XXXXXX" ) == string::npos )
    {
        name += "XXXXXX";
    }
    m_name = strdup( name.c_str() );
    mkstemp( m_name );

    static_cast< filebuf* >( rdbuf() )->open( m_name, mode );
}

/**
 * Destroys the temporary file object. The associated file is closed and deleted.
 */
TemporaryFile::~TemporaryFile()
{
    delete rdbuf();
    unlink( m_name );
    delete m_name;
}

/**
 * Returns the complete unique file name.
 */
string TemporaryFile::name() const
{
    return m_name;
}

/**
 * Closes the file.
 */
void TemporaryFile::close()
{
    static_cast< filebuf* >( rdbuf() )->close();
}
