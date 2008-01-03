#include "metric.h"

#include <CORBA.h>

using namespace Alitheia;

char* Metric::getAuthor()
{
    return CORBA::string_dup( "Metric::getAuthor()" );
}

char* Metric::getDescription()
{
    return CORBA::string_dup( "Metric::getDescription()" );
}

char* Metric::getName()
{
    return CORBA::string_dup( "Metric::getName()" );
}

char* Metric::getVersion()
{
    return CORBA::string_dup( "Metric::getVersion()" );
}

char* Metric::getResult()
{
    return CORBA::string_dup( "Metric::getResult()" );
}

char* Metric::getDateInstalled()
{
    return CORBA::string_dup( "Metric::getDateInstalled()" );
}
