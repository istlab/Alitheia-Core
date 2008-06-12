#include "fds.h"

#include "dbobject.h"
#include "corbahandler.h"

#include <exception>
#include <fstream>

#include <boost/bind.hpp>
#include <boost/filesystem/operations.hpp>

namespace Alitheia
{
    /**
     * \internal
     */
    class FDS::Private
    {
    public:
        Private( FDS* q )
            : q( q )
        {
        }
        
    private:
        FDS* const q;

    public:
        eu::sqooss::impl::service::corba::alitheia::FDS_var fds;
    };
}

using namespace Alitheia;
using namespace eu::sqooss::impl::service::corba;
using namespace boost;
using std::cerr;
using std::endl;
using std::exception;
using std::string;
using std::vector;
using std::ofstream;

/**
 * Creates a new Checkout from it's CORBA representation.
 */
Checkout::Checkout( const eu::sqooss::impl::service::corba::alitheia::Checkout& checkout )
    : version( checkout.version )
{
    const uint length = checkout.files.length();
    for( uint i = 0; i < length; ++i )
    {
        files.push_back( ProjectFile( checkout.files[ i ] ) );
    }
}

/**
 * Destroys the Checkout.
 */
Checkout::~Checkout()
{
}

/**
 * Saves the file \a file into \a directory.
 * The target file path even contains the file's directory.
 * The directories are created if they don't exist yet.
 */
void Checkout::saveFile( const string& directory, const ProjectFile& file ) const
{
    const string dirname = directory + "/" + file.directory.path;
    const string filename = dirname + "/" + file.name;
    boost::filesystem::create_directory( dirname );
    file.save( filename );
}

/**
 * Saves the complete checkout into \a directory.
 */
void Checkout::save( const std::string& directory ) const
{
    for_each( files.begin(), files.end(), bind( &Checkout::saveFile, this, directory, _1 ) );
}

/**
 * Creates a new FDS.
 */
FDS::FDS()
    : d( new Private( this ) )
{
    try
    {
        d->fds = alitheia::FDS::_narrow( CorbaHandler::instance()->getObject( "AlitheiaFDS" ) );
    }
    catch( ... )
    {
         cerr << "Got an exception while getting an instance of the FDS. Make sure the Alitheia system is running and eu.sqooss.service.corbaservice is loaded." << endl;
        throw exception();
    }
}

/**
 * Destroys the FDS.
 */
FDS::~FDS()
{
    delete d;
}

/**
 * Returns the complete contents of \a file.
 */
string FDS::getFileContents( const ProjectFile& file ) const
{
    const alitheia::bytes* const content = d->fds->getFileContents( file.toCorba() );
    const size_t size = content->length();
    std::string result;
    if( size == 0 )
        return result;
    const unsigned char* const buffer = content->get_buffer();
    for_each( buffer, buffer + size, bind( &string::push_back, &result, _1 ) );
    return result;
}

/**
 * Returns \a length bytes of the contents of \a file beginning at \a begin.
 */
string FDS::getFileContents( const ProjectFile& file, int begin, int length ) const
{
    const alitheia::bytes* const content = d->fds->getFileContentParts( file.toCorba(), begin, length );
    const size_t size = content->length();
    std::string result;
    if( size == 0 )
        return result;
    const unsigned char* const buffer = content->get_buffer();
    for_each( buffer, buffer + size, bind( &string::push_back, &result, _1 ) );
    return result;
}

/**
 * Creates a Checkout of ProjectVersion \a version.
 * If \a pattern is set, only files with path name matching the regular expression
 * are contained in the Checkout.
 */
Checkout FDS::getCheckout( const ProjectVersion& version, const string& pattern ) const
{
    return Checkout( *(d->fds->getCheckout( version.toCorba(), pattern.empty() ? ".*"
                                                                               : CORBA::string_dup( pattern.c_str() ) ) ) );
}
