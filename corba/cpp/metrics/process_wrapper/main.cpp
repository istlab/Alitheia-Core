#include <Core>

#include "wrappermetric.h"


#include <iostream>
#include <string>
#include <exception>
#include <vector>
#include <cassert>

#include <boost/program_options/options_description.hpp>
#include <boost/program_options/parsers.hpp>
#include <boost/program_options/variables_map.hpp>

using namespace boost::program_options;
using std::cerr;
using std::cout;
using std::endl;
using std::string;
using std::exception;
using std::vector;

using Alitheia::Core;

template<typename T>
std::ostream& operator<<( std::ostream& stream, const vector< T >& v )
{
    for( typename vector< T >::const_iterator it = v.begin(); it != v.end(); ++it )
        stream << *it << ' ';
    return stream;
}

int main( int argc, char* argv[] )
{
    options_description desc( "Allowed options" );
    desc.add_options()
        ( "help", "produce help message" )
        ( "type", value< string >(), "the type of the metric \n\"ProjectFile\" or \n\"ProjectVersion\"" )
        ( "command", value< vector< string > >()->multitoken(), "the command used for execution" )
    ;
    positional_options_description pd;
    pd.add( "command", -1 );

    string type;
    string program;
    vector< string > arguments;

    variables_map vm;
    try
    {
        store( command_line_parser( argc, argv ).positional( pd ).options( desc ).run(), vm );
        notify( vm );

        if( vm.count( "help" ) )
        {
            cout << desc << endl;
            return 1;
        }

        type = vm[ "type" ].as< string >();

        arguments = vm[ "command" ].as< vector< string > >();
        // boost makes sure, that arguments isn't empty
        assert( !arguments.empty() );
        program = arguments.front();
        arguments.erase( arguments.begin() );
    }
    catch( const exception& e )
    {
        cerr << e.what() << endl;
        return 1;
    }

    if( type == "ProjectFile" )
        Core::instance()->registerMetric( new ProjectFileWrapperMetric( program, arguments ) );
    else if( type == "ProjectVersion" )
        Core::instance()->registerMetric( new ProjectVersionWrapperMetric( program, arguments ) );
    else
    {
        cerr << "unknown type: '" << type << "'" << endl;
        return 1;
    }

    Core::instance()->run();
    return 0;
}
