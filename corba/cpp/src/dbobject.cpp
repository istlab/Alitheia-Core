#include "dbobject.h"

#include "core.h"
#include "fds.h"
#include "database.h"

#include "Alitheia.h"

#include "CORBA.h"

#include <sstream>
#include <fstream>
#include <vector>

#include <boost/date_time/posix_time/posix_time.hpp>

using namespace Alitheia;
using namespace eu::sqooss::impl::service::corba;
using namespace boost::posix_time;

using std::string;
using std::istream;
using std::stringbuf;
using std::ostream;
using std::ofstream;
using std::vector;

DAObject::DAObject( int id )
    : id( id )
{
}

DAObject::~DAObject()
{
}

StoredProject::StoredProject( const alitheia::StoredProject& project )
    : DAObject( project.id ),
      name( project.name ),
      website( project.website ),
      contact( project.contact ),
      repository( project.repository ),
      mail( project.mail )
{
}

alitheia::StoredProject StoredProject::toCorba() const
{
    alitheia::StoredProject result;
    result.id = id;
    result.name = CORBA::string_dup( name.c_str() );
    result.website = CORBA::string_dup( website.c_str() );
    result.contact = CORBA::string_dup( contact.c_str() );
    result.repository = CORBA::string_dup( repository.c_str() );
    return result;
}

StoredProject::operator CORBA::Any() const
{
    CORBA::Any any;
    any <<= toCorba();
    return any;
}

StoredProject StoredProject::fromCorba( const CORBA::Any& any )
{
    alitheia::StoredProject project;
    any >>= project;
    return StoredProject( project );
}

StoredProject StoredProject::getProjectByName( const std::string& name )
{
    Database db;
    Database::property_map properties;
    properties[ "name" ] = name;
    const vector< StoredProject > projects = db.findObjectsByProperties< StoredProject >( properties );
    return projects.empty() ? StoredProject() : projects.front();
}

vector< Bug > StoredProject::getBugs() const
{
    return Core::instance()->getBugs( *this );
}

ProjectVersion StoredProject::getLastProjectVersion( const StoredProject& project )
{
    Database db;
    Database::property_map properties;
    properties[ "sp" ] = project;
    const vector< Database::db_row_entry > versions = db.doHQL(
        "from ProjectVersion pv where pv.project=:sp "
        "and pv.version = ( select max( pv2.version ) from "
        "ProjectVersion pv2 where pv2.project=:sp)", properties );
    return versions.empty() ? ProjectVersion() : boost::get< ProjectVersion >( versions.front() );
}
 
ProjectVersion::ProjectVersion( const alitheia::ProjectVersion& version )
    : DAObject( version.id ),
      project( version.project ),
      version( version.version ),
      timeStamp( version.timeStamp ),
      committer( version.committer ),
      commitMsg( version.commitMsg ),
      properties( version.properties )
{
}

alitheia::ProjectVersion ProjectVersion::toCorba() const
{
    alitheia::ProjectVersion result;
    result.id = id;
    result.project = project.toCorba();
    result.version = CORBA::string_dup( version.c_str() );
    result.timeStamp = timeStamp;
    result.committer = committer.toCorba();
    result.commitMsg = CORBA::string_dup( commitMsg.c_str() );
    result.properties = CORBA::string_dup( properties.c_str() );
    return result;
}

ProjectVersion::operator CORBA::Any() const
{
    CORBA::Any any;
    any <<= toCorba();
    return any;
}

ProjectVersion ProjectVersion::fromCorba( const CORBA::Any& any )
{
    alitheia::ProjectVersion version;
    any >>= version;
    return ProjectVersion( version );
}

std::vector< ProjectFile > ProjectVersion::getVersionFiles() const
{
    return Core::instance()->getVersionFiles( *this );
}

namespace Alitheia
{
class ProjectFileBuffer : public stringbuf
{
public:
    ProjectFileBuffer( const ProjectFile* file )
        : file( file ),
          eof( false ),
          readBytes( 0 )
    {
    }

protected:
    int underflow()
    {
        if( !eof )
        {
            static FDS fds;
            // read the data in blocks of 16kb
            string data = fds.getFileContents( *file, readBytes, 16384 );
            sputn( data.c_str(), data.size() );
            readBytes += data.size();
            eof = data.size() == 0;
        }
        else
        {
            return EOF;
        }
        return stringbuf::underflow();
    }

private:
    const ProjectFile* const file;
    bool eof;
    int readBytes;
};
}

ProjectFile::ProjectFile()
    : istream( new ProjectFileBuffer( this ) ),
      DAObject( 0 ),
      isDirectory( false )
{
}

ProjectFile::ProjectFile( const alitheia::ProjectFile& file )
    : istream( new ProjectFileBuffer( this ) ),
      DAObject( file.id ),
      name( file.name ),
      projectVersion( file.version ),
      status( file.status ),
      isDirectory( file.isDirectory ),
      directory( file.dir )
{
}

ProjectFile::ProjectFile( const ProjectFile& other )
    : istream( new ProjectFileBuffer( this ) )
{
    *this = other;
}

ProjectFile& ProjectFile::operator=( const ProjectFile& other )
{
    id = other.id;
    name = other.name;
    projectVersion = other.projectVersion;
    status = other.status;
    isDirectory = other.isDirectory;
    directory = other.directory;
    return *this;
}

void ProjectFile::save( ostream& stream ) const
{
    // copy intented
    ProjectFile projectFile = *this;
    string line;
    do
    {
        std::getline( projectFile, line );
        if( !projectFile.eof() )
            line.push_back( '\n' );
        stream.write( line.c_str(), line.size() );
    } while( !projectFile.eof() );
}

void ProjectFile::save( const string& filename ) const
{
    ofstream file( filename.c_str() );
    save( file );
}

std::string ProjectFile::getFileName() const
{
    if( directory.path == "/" )
        return directory.path + name;
    else
        return directory.path + "/" + name;
}

alitheia::ProjectFile ProjectFile::toCorba() const
{
    alitheia::ProjectFile result;
    result.id = id;
    result.name = CORBA::string_dup( name.c_str() );
    result.version = projectVersion.toCorba();
    result.status = CORBA::string_dup( status.c_str() );
    result.isDirectory = isDirectory;
    result.dir = directory.toCorba();
    return result;
}

ProjectFile::operator CORBA::Any() const
{
    CORBA::Any any;
    any <<= toCorba();
    return any;
}

ProjectFile ProjectFile::fromCorba( const CORBA::Any& any )
{
    alitheia::ProjectFile file;
    any >>= file;
    return ProjectFile( file );
}

ProjectFile::~ProjectFile()
{
    delete rdbuf();
}

FileGroup::FileGroup( const alitheia::FileGroup& group )
    : DAObject( group.id ),
      name( group.name ),
      subPath( group.subPath ),
      regex( group.regex ),
      recalcFreq( group.recalcFreq ),
      lastUsed( group.lastUsed ),
      projectVersion( group.version )
{
}

alitheia::FileGroup FileGroup::toCorba() const
{
    alitheia::FileGroup result;
    result.id = id;
    result.name = CORBA::string_dup( name.c_str() );
    result.subPath = CORBA::string_dup( subPath.c_str() );
    result.regex = CORBA::string_dup( regex.c_str() );
    result.recalcFreq = recalcFreq;
    result.lastUsed = CORBA::string_dup( lastUsed.c_str() );
    result.version = projectVersion.toCorba();
    return result;
}

FileGroup::operator CORBA::Any() const
{
    CORBA::Any any;
    any <<= toCorba();
    return any;
}

FileGroup FileGroup::fromCorba( const CORBA::Any& any )
{
    alitheia::FileGroup group;
    any >>= group;
    return FileGroup( group );
}

BugResolution::BugResolution( const alitheia::BugResolution& res )
    : DAObject( res.id ),
      resolution( res.resolution )
{
}

alitheia::BugResolution BugResolution::toCorba() const
{
    alitheia::BugResolution result;
    result.id = id;
    result.resolution = CORBA::string_dup( resolution.c_str() );
    return result;
}

BugResolution::operator CORBA::Any() const
{
    CORBA::Any any;
    any <<= toCorba();
    return any;
}

BugResolution BugResolution::fromCorba( const CORBA::Any& any )
{
    alitheia::BugResolution bug;
    any >>= bug;
    return BugResolution( bug );
}

BugPriority::BugPriority( const alitheia::BugPriority& res )
    : DAObject( res.id ),
      priority( res.priority )
{
}

alitheia::BugPriority BugPriority::toCorba() const
{
    alitheia::BugPriority result;
    result.id = id;
    result.priority = CORBA::string_dup( priority.c_str() );
    return result;
}

BugPriority::operator CORBA::Any() const
{
    CORBA::Any any;
    any <<= toCorba();
    return any;
}

BugPriority BugPriority::fromCorba( const CORBA::Any& any )
{
    alitheia::BugPriority bug;
    any >>= bug;
    return BugPriority( bug );
}

BugSeverity::BugSeverity( const alitheia::BugSeverity& res )
    : DAObject( res.id ),
      severity( res.severity )
{
}

alitheia::BugSeverity BugSeverity::toCorba() const
{
    alitheia::BugSeverity result;
    result.id = id;
    result.severity = CORBA::string_dup( severity.c_str() );
    return result;
}

BugSeverity::operator CORBA::Any() const
{
    CORBA::Any any;
    any <<= toCorba();
    return any;
}

BugSeverity BugSeverity::fromCorba( const CORBA::Any& any )
{
    alitheia::BugSeverity bug;
    any >>= bug;
    return BugSeverity( bug );
}

Bug::Bug( const alitheia::Bug& bug )
    : DAObject( bug.id ),
      project( bug.project ),
      updateRun( from_iso_string( string( bug.updateRun ) ) ),
      bugId( bug.bugId ),
      creationTS( from_iso_string( string( bug.creationTS ) ) ),
      deltaTS( from_iso_string( string( bug.deltaTS ) ) ),
      reporter( bug.reporter),
      resolution( bug.resolution ),
      priority( bug.priority ),
      severity( bug.severity ),
      shortDesc( bug.shortDesc )
{
}

alitheia::Bug Bug::toCorba() const
{
    alitheia::Bug result;
    result.id = id;
    result.project = project.toCorba();
    result.updateRun = CORBA::string_dup( to_iso_string( updateRun ).c_str() );
    result.bugId = CORBA::string_dup( bugId.c_str() );
    result.creationTS= CORBA::string_dup( to_iso_string( creationTS ).c_str() );
    result.deltaTS = CORBA::string_dup( to_iso_string( deltaTS ).c_str() );
    result.reporter = reporter.toCorba();
    result.resolution = resolution.toCorba();
    result.priority = priority.toCorba();
    result.severity = severity.toCorba();
    result.shortDesc = CORBA::string_dup( shortDesc.c_str() );
    return result;
}

Bug::operator CORBA::Any() const
{
    CORBA::Any any;
    any <<= toCorba();
    return any;
}

Bug Bug::fromCorba( const CORBA::Any& any )
{
    alitheia::Bug bug;
    any >>= bug;
    return Bug( bug );
}

MetricType::MetricType( const alitheia::MetricType& type )
    : DAObject( type.id ),
      type( static_cast< Type >( type.type ) )
{
}

alitheia::MetricType MetricType::toCorba() const
{
    alitheia::MetricType result;
    result.id = id;
    result.type = static_cast< alitheia::MetricTypeType >( type );
    return result;
}

MetricType::operator CORBA::Any() const
{
    CORBA::Any any;
    any <<= toCorba();
    return any;
}

MetricType MetricType::fromCorba( const CORBA::Any& any )
{
    alitheia::MetricType type;
    any >>= type;
    return MetricType( type );
}

MetricType MetricType::getMetricType( Type t )
{
    Database db;
    Database::property_map properties;
    switch( t )
    {
    case SourceCode:
        properties[ "type" ] = string( "SOURCE_CODE" );
        break;
    case MailingList:
        properties[ "type" ] = string( "MAILING_LIST" );
        break;
    case BugDatabase:
        properties[ "type" ] = string( "BUG_DATABASE" );
        break;
    case ProjectWide:
        properties[ "type" ] = string( "PROJECT_WIDE" );
        break;
    }
    vector< MetricType > types = db.findObjectsByProperties< MetricType >( properties );
    if( !types.empty() )
        return types.front();

    MetricType type;
    type.type = t;
    db.addRecord( type );
    return type;
}

Plugin::Plugin( const alitheia::Plugin& plugin )
    : DAObject( plugin.id ),
      name( plugin.name ),
      installdate( plugin.installdate )
{
}

alitheia::Plugin Plugin::toCorba() const
{
    alitheia::Plugin result;
    result.id = id;
    result.name = CORBA::string_dup( name.c_str() );
    result.installdate = CORBA::string_dup( installdate.c_str() );
    return result;
}

Plugin::operator CORBA::Any() const
{
    CORBA::Any any;
    any <<= toCorba();
    return any;
}

Plugin Plugin::fromCorba( const CORBA::Any& any )
{
    alitheia::Plugin plugin;
    any >>= plugin;
    return Plugin( plugin );
}

PluginConfiguration::PluginConfiguration( const alitheia::PluginConfiguration& config )
    : DAObject( config.id ),
      name( config.name ),
      value( config.value ),
      type( config.type ),
      msg( config.msg ),
      plugin( config.metricPlugin )
{
}

PluginConfiguration PluginConfiguration::fromCorba( const CORBA::Any& any )
{
    alitheia::PluginConfiguration config;
    any >>= config;
    return PluginConfiguration( config );
}

alitheia::PluginConfiguration PluginConfiguration::toCorba() const
{
    alitheia::PluginConfiguration result;
    result.id = id;
    result.name = CORBA::string_dup( name.c_str() );
    result.value = CORBA::string_dup( value.c_str() );
    result.type = CORBA::string_dup( type.c_str() );
    result.msg = CORBA::string_dup( msg.c_str() );
    result.metricPlugin = plugin.toCorba();
    return result;
}

PluginConfiguration::operator CORBA::Any() const
{
    CORBA::Any any;
    any <<= toCorba();
    return any;
}

Metric::Metric( const alitheia::Metric& metric )
    : DAObject( metric.id ),
      plugin( metric.metricPlugin ),
      metricType( metric.type ),
      mnemonic( metric.mnemonic ),
      description( metric.description )
{
}

alitheia::Metric Metric::toCorba() const
{
    alitheia::Metric result;
    result.id = id;
    result.metricPlugin = plugin.toCorba();
    result.type = metricType.toCorba();
    result.mnemonic = CORBA::string_dup( mnemonic.c_str() );
    result.description = CORBA::string_dup( description.c_str() );
    return result;
}

Metric::operator CORBA::Any() const
{
    CORBA::Any any;
    any <<= toCorba();
    return any;
}

Metric Metric::fromCorba( const CORBA::Any& any )
{
    alitheia::Metric metric;
    any >>= metric;
    return Metric( metric );
}

ProjectFileMeasurement::ProjectFileMeasurement( const alitheia::ProjectFileMeasurement& measurement )
    : DAObject( measurement.id ),
      metric( measurement.measureMetric ),
      projectFile( measurement.file ),
      result( measurement.result )
{
}

alitheia::ProjectFileMeasurement ProjectFileMeasurement::toCorba() const
{
    alitheia::ProjectFileMeasurement result;
    result.id = id;
    result.measureMetric = metric.toCorba();
    result.file = projectFile.toCorba();
    result.result = CORBA::string_dup( this->result.c_str() );
    return result;
}

ProjectFileMeasurement::operator CORBA::Any() const
{
    CORBA::Any any;
    any <<= toCorba();
    return any;
}

ProjectFileMeasurement ProjectFileMeasurement::fromCorba( const CORBA::Any& any )
{
    alitheia::ProjectFileMeasurement measurement;
    any >>= measurement;
    return ProjectFileMeasurement( measurement );
}

ProjectVersionMeasurement::ProjectVersionMeasurement( const alitheia::ProjectVersionMeasurement& measurement )
    : DAObject( measurement.id ),
      metric( measurement.measureMetric ),
      projectVersion( measurement.version ),
      result( measurement.result )
{
}

alitheia::ProjectVersionMeasurement ProjectVersionMeasurement::toCorba() const
{
    alitheia::ProjectVersionMeasurement result;
    result.id = id;
    result.measureMetric = metric.toCorba();
    result.version = projectVersion.toCorba();
    result.result = CORBA::string_dup( this->result.c_str() );
    return result;
}

ProjectVersionMeasurement::operator CORBA::Any() const
{
    CORBA::Any any;
    any <<= toCorba();
    return any;
}

ProjectVersionMeasurement ProjectVersionMeasurement::fromCorba( const CORBA::Any& any )
{
    alitheia::ProjectVersionMeasurement measurement;
    any >>= measurement;
    return ProjectVersionMeasurement( measurement );
}

Developer::Developer( const alitheia::Developer& developer )
    : DAObject( developer.id ),
      name( developer.name ),
      email( developer.email ),
      username( developer.username ),
      storedProject( developer.project )
{
}

alitheia::Developer Developer::toCorba() const
{
    alitheia::Developer result;
    result.id = id;
    result.name = CORBA::string_dup( name.c_str() );
    result.email = CORBA::string_dup( email.c_str() );
    result.username = CORBA::string_dup( username.c_str() );
    result.project = storedProject.toCorba();
    return result;
}

Developer::operator CORBA::Any() const
{
    CORBA::Any any;
    any <<= toCorba();
    return any;
}

Developer Developer::fromCorba( const CORBA::Any& any )
{
    alitheia::Developer dev;
    any >>= dev;
    return Developer( dev );
}

Developer Developer::byEmail( const std::string& email, const StoredProject& sp )
{
    Database db;
    Database::property_map properties;
    properties[ "email" ] = email;
    properties[ "storedProject" ] = sp;
    vector< Developer > devs = db.findObjectsByProperties< Developer >( properties );

    if( !devs.empty() )
        return devs.front();

    properties.clear();

    const size_t atPos = email.find( '@' );
    if( atPos == string::npos )
        return Developer();

    properties[ "username" ] = email.substr( 0, atPos );
    properties[ "storedProject" ] = sp;
    devs = db.findObjectsByProperties< Developer >( properties );

    if( !devs.empty() )
    {
        Developer& d = devs.front();
        d.email = email;
        db.updateRecord( d );
        return d;
    }

    Developer d;
    d.email = email;
    d.storedProject = sp;

    db.addRecord( d );

    return d;
}

Developer Developer::byUsername( const std::string& username, const StoredProject& sp )
{
    Database db;
    Database::property_map properties;
    properties[ "username" ] = username;
    properties[ "storedProject" ] = sp;
    vector< Developer > devs = db.findObjectsByProperties< Developer >( properties );
    
    if( !devs.empty() )
        return devs.front();

    properties.clear();
    properties[ "username" ] = username + '%';
    const vector< Database::db_row_entry > entries = db.doHQL( "from Developer dev "
                                                               "where dev.email like :username",
                                                               properties );
    for( vector< Database::db_row_entry >::const_iterator it = entries.begin(); it != entries.end(); ++it )
    {
        const Developer dev = boost::get<Developer>( *it );
        if( dev.email.substr( 0, username.length() ) == username )
            return dev;
    }

    Developer d;
    d.username = username;
    d.storedProject = sp;

    db.addRecord( d );

    return d;
}


Directory::Directory( const alitheia::Directory& directory )
    : DAObject( directory.id ),
      path( directory.path )
{
}

alitheia::Directory Directory::toCorba() const
{
    alitheia::Directory result;
    result.id = id;
    result.path = CORBA::string_dup( path.c_str() );
    return result;
}

Directory::operator CORBA::Any() const
{
    CORBA::Any any;
    any <<= toCorba();
    return any;
}

Directory Directory::fromCorba( const CORBA::Any& any )
{
    alitheia::Directory dir;
    any >>= dir;
    return Directory( dir );
}

Directory Directory::getDirectory( const std::string& path )
{
    Database db;
    Database::property_map properties;
    properties[ "path" ] = path;

    const vector< Directory > dirs = db.findObjectsByProperties< Directory >( properties );
    if( !dirs.empty() )
        return dirs.front();

    Directory d;
    d.path = path;
    db.addRecord( d );
    return d;
}
