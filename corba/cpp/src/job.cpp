#include "job.h"

#include <CORBA.h>

#include <iostream>

#include "core.h"

namespace Alitheia
{
    class Job::Private
    {
    public:
        Private( Job* q );
        ~Private();

    private:
        Job* q;

    public:
        std::string name;
    };
}

using namespace Alitheia;

Job::Private::Private( Job* q )
    : q( q )
{
}

Job::Private::~Private()
{
}

Job::Job()
    : d( new Private( this ) )
{
}

Job::~Job()
{
    delete d;
}

CORBA::Long Job::priority()
{
    return 0;
}

void Job::run()
{
}

void Job::addDependency( Job* other )
{
    Core::instance()->addJobDependency( this, other );
}

const std::string& Job::name() const
{
    return d->name;
}

void Job::setName( const std::string& name )
{
    d->name = name;
}
