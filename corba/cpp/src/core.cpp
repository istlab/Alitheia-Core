#include "core.h"

#include "corbahandler.h"
#include "job.h"
#include "metric.h"
#include "dbobject.h"

#include <csignal>
#include <string>
#include <sstream>

#include <boost/thread.hpp>

namespace Alitheia
{
    /**
     * \internal
     */
    class Core::Private
    {
    public:
        Private( Core* q );

    private:
        Core* q;

    public:
        eu::sqooss::impl::service::corba::alitheia::Core_var core;
        std::map< int, std::string > registeredMetrics;
    };
}

using namespace std;
using boost::mutex;
using namespace Alitheia;
using namespace eu::sqooss::impl::service::corba;

// singleton object
static Core* core = 0;
static mutex core_mutex;

Core::Private::Private( Core* q )
    : q( q )
{
};

/**
 * Ctrl+C handler for shutting down the Core.
 */
static void signal_handler( int sig )
{
    if( sig == SIGINT )
    {
        delete Core::instance();
        exit( 1 );
    }
}

/**
 * Creates a new instance of the Core.
 */
Core::Core()
    : d( new Private( this ) )
{
    // install signal handler to shutdown nicely
    signal( SIGINT, signal_handler );

    try
    {
        d->core = alitheia::Core::_narrow( CorbaHandler::instance()->getObject( "AlitheiaCore" ) );
    }
    catch( CORBA::SystemException_catch& ex )
    {
        cerr << "Exception in Core::Core():" << endl;
        ex->_print( cerr );
        cerr << endl;
        throw ex;
    }
    catch( ... )
    {
        cerr << "dumdidum?" << endl;
    }
}

/**
 * Destroys the Core.
 */
Core::~Core()
{
    shutdown();
    core = 0;
    delete d;
}

/**
 * Returns the singleton instance of the Core.
 */
Core* Core::instance()
{
    if( core == 0 )
        core = new Core();
    return core;
}

/**
 * Registers \a metric in the Alitheia system.
 */
int Core::registerMetric( AbstractMetric* metric )
{
    std::stringstream ss;
    ss << "Alitheia_Metric_" << getUniqueId();
    const std::string name = ss.str();
    metric->setOrbName( name );
    CorbaHandler::instance()->exportObject( metric->_this(), name.c_str() );
    const int id = d->core->registerMetric( CORBA::string_dup( name.c_str() ) );
    metric->setId( id );
    d->registeredMetrics[ id ] = name;
    return id;
}

/**
 * Returns an unique id.
 * This is used by the Core and by the Scheduler to create unique
 * names for objects in the ORB.
 */
int Core::getUniqueId() const
{
    return d->core->getUniqueId();
}

/**
 * Unregisters \a metric from the Alitheia system.
 */
void Core::unregisterMetric( AbstractMetric* metric )
{
    unregisterMetric( metric->id() );
}

/**
 * Unregisters a metric with \a id from the Alitheia system.
 */
void Core::unregisterMetric( int id )
{
    CorbaHandler::instance()->unexportObject( CORBA::string_dup( d->registeredMetrics[ id ].c_str() ) );
    d->core->unregisterMetric( id );
    d->registeredMetrics.erase( id );
}

/**
 * \internal
 * Add a supported metric description to the database.
 * This method is only used by AbstractMetric.
 * Call AbstractMetric::addSupportedMetrics instead.
 */
bool Core::addSupportedMetrics( AbstractMetric* metric, const std::string& description, 
                                const std::string& mnemonic, MetricType::Type type ) const
{
    return d->core->addSupportedMetrics( CORBA::string_dup( metric->orbName().c_str() ), 
                                         CORBA::string_dup( description.c_str() ),
                                         CORBA::string_dup( mnemonic.c_str() ),
                                         static_cast< alitheia::MetricTypeType >( type ) );
}

/**
 * \internal
 * Get the dscription objects for all metrics supported by \a metric.
 * This method is only used by AbstractMetric.
 * Call AbstractMetrig::getSupportedMetrics instead.
 */
vector< Metric > Core::getSupportedMetrics( const AbstractMetric* metric ) const
{
    const alitheia::MetricList& metrics = *(d->core->getSupportedMetrics( CORBA::string_dup( metric->orbName().c_str() ) ));

    vector< Metric > result;

    const uint length = metrics.length();
    for( uint i = 0; i < length; ++i )
        result.push_back( Metric( metrics[ i ] ) );

    return result;
}

vector< ProjectFile > Core::getVersionFiles( const ProjectVersion& version ) const
{
    const alitheia::ProjectFileList& files = *(d->core->getVersionFiles( version.toCorba() ) );

    vector< ProjectFile > result;

    const uint length = files.length();
    for( uint i = 0; i < length; ++i )
    {
        result.push_back( ProjectFile( files[ i ] ) );
    }

    return result;
}

vector< Bug > Core::getBugs( const StoredProject& project ) const
{
    const alitheia::BugList& bugs = *(d->core->getBugs( project.toCorba() ) );

    vector< Bug > result;

    const uint length = bugs.length();
    for( uint i = 0; i < length; ++i )
    {
        result.push_back( Bug( bugs[ i ] ) );
    }

    return result;
}

/**
 * Runs the Alitheia CORBA system.
 * Blocks until shutdown is called.
 */
void Core::run()
{
    CorbaHandler::instance()->run();
}

/**
 * Shuts down the Alitheia CORBA system.
 * All registered metrics will be unregistered.
 */
void Core::shutdown()
{
    for( map< int, string >::iterator it = d->registeredMetrics.begin(); it != d->registeredMetrics.end(); ++it )
    {
        unregisterMetric( it->first );
    }
}
