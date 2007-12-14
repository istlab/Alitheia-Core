#include "logger.h"

using namespace Alitheia;

int main( int argc, char **argv)
{
    Logger l( Logger::NameSqoOssMetric );
    l.info( "foobar!" );
}
