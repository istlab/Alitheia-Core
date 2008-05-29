#include "fds.h"

#include "dbobject.h"
#include "corbahandler.h"

#include <exception>
#include <fstream>

#include <boost/bind.hpp>
#include <boost/filesystem/operations.hpp>

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
using namespace boost;
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

void Checkout::saveFile( const string& directory, const ProjectFile& file ) const
{
    const string dirname = directory + "/" + file.directory.path;
    const string filename = dirname + "/" + file.name;
    boost::filesystem::create_directory( dirname );
    file.save( filename );
}

void Checkout::save( const std::string& directory ) const
{
    for_each( files.begin(), files.end(), bind( &Checkout::saveFile, this, directory, _1 ) );
}

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

Checkout FDS::getCheckout( const ProjectVersion& version, const string& pattern ) const
{
    return Checkout( *(d->fds->getCheckout( version.toCorba(), pattern.empty() ? ".*"
                                                                               : CORBA::string_dup( pattern.c_str() ) ) ) );
}
