#include <Logger>

#include <sstream>

using namespace std;
using namespace Alitheia;

int main( int argc, char **argv)
{
    if( argc < 3 )
    {
        cout << "Usage: " << argv[ 0 ] << " <logger> <message>" << endl;
        cout << "Where <logger> is one of:" << endl;
        cout << "sqooss" << endl;
        cout << "sqooss.service" << endl;
        cout << "sqooss.database" << endl;
        cout << "sqooss.security" << endl;
        cout << "sqooss.messaging" << endl;
        cout << "sqooss.webservices" << endl;
        cout << "sqooss.scheduler" << endl;
        cout << "sqooss.updater" << endl;
        cout << "sqooss.webadmin" << endl;
        cout << "sqooss.tds" << endl;
        cout << "sqooss.fds" << endl;
        cout << "sqooss.metric" << endl;
        cout << "sqooss.tester" << endl;
        return 1;
    }
    
    Logger l( argv[ 1 ] );

    stringstream ss;
    for( int i = 2; i < argc; ++ i )
    {
        ss << argv[ i ];
        if( i + 1 != argc )
            ss << " ";
    }

    l.info( ss.str() );

    return 0;
}
