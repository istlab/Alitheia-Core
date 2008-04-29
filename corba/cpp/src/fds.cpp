#include "fds.h"

#include "dbobject.h"
#include "corbahandler.h"

#include <exception>
#include <fstream>

#include <sys/stat.h>
#include <sys/types.h>

namespace Alitheia
{
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
using std::cerr;
using std::endl;
using std::exception;
using std::string;
using std::vector;
using std::ofstream;

Checkout::Checkout( const eu::sqooss::impl::service::corba::alitheia::Checkout& checkout )
    : version( checkout.version )
{
    const uint length = checkout.files.length();
    for( uint i = 0; i < length; ++i )
    {
        files.push_back( ProjectFile( checkout.files[ i ] ) );
    }
}

Checkout::~Checkout()
{
}


void Checkout::save( const std::string& directory ) const
{
    for( vector< ProjectFile >::const_iterator it = files.begin(); it != files.end(); ++it )
    {
        // copy intented
        ProjectFile projectFile = *it;
        string line;
        const string dirname = directory + "/" + projectFile.directory.path;
        const string filename = dirname + "/" + projectFile.name;
        mkdir( dirname.c_str(), S_IRWXU );
        ofstream file( filename.c_str() );
        do
        {
            std::getline( projectFile, line );
            if( !projectFile.eof() )
                line.push_back( '\n' );
            file.write( line.c_str(), line.size() );
        } while( !projectFile.eof() );
    }
}

FDS::FDS()
    : d( new Private( this ) )
{
    try
    {
        d->fds = alitheia::FDS::_narrow( CorbaHandler::instance()->getObject( "FDS" ) );
    }
    catch( ... )
    {
         cerr << "Got an exception while getting an instance of the FDS. Make sure the Alitheia system is running and eu.sqooss.service.corbaservice is loaded." << endl;
        throw exception();
    }
}

FDS::~FDS()
{
    delete d;
}

string FDS::getFileContents( const ProjectFile& file ) const
{
    CORBA::String_var content;
    const int length = d->fds->getFileContents( file.toCorba(), content.out() );
    return std::string( content, length );
}

Checkout FDS::getCheckout( const ProjectVersion& version ) const
{
    return Checkout( *(d->fds->getCheckout( version.toCorba() ) ) );
}
