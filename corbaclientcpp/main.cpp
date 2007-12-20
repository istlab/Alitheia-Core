#include "logger.h"
#include "core.h"

using namespace Alitheia;

int main( int argc, char **argv)
{
    Core c;
    Logger l( Logger::NameSqoOssMetric );
    l.info( "foobar!" );
}
