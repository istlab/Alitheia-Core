#include <QCoreApplication>

#include <Core>

#include "wrappermetric.h"

using Alitheia::Core;

#include <iostream>

int main( int argc, char **argv)
{
    if( argc == 1 )
    {
        std::cerr << "Usage: " << argv[ 0 ] << " <command>" << std::endl;
        return 1;
    }

    QCoreApplication app( argc, argv );
    Core::instance()->registerMetric( new ProjectFileWrapperMetric );
    Core::instance()->run();
    return 0;
}
