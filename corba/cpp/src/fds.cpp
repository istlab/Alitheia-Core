#include "fds.h"

#include "dbobject.h"
#include "corbahandler.h"

#include <exception>

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
