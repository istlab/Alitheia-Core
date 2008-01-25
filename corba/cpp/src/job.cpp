#include "job.h"

#include <CORBA.h>

#include <iostream>

using namespace Alitheia;
using namespace std;

CORBA::Long Job::priority()
{
    return 0;
}

void Job::run()
{
}
