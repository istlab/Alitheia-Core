#include <Core>

#include "wcmetric.h"

using Alitheia::Core;

int main( int argc, char **argv)
{
    Core::instance()->registerMetric( new WcMetric );
    Core::instance()->run();
}
