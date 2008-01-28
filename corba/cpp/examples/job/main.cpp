#include <Logger>
#include <Core>
#include <Job>

#include <sstream>
#include <ostream>

using namespace std;
using namespace Alitheia;

class TestJob : public Job
{
public:
    TestJob( Logger& l, const string& name )
        : l( l ),
          name( name )
    {
    }
    
    void run()
    {
        for( int i = 0; i < 10; ++i )
        {
            for( long long bogus = 0; bogus < 1000000000LL; ++bogus )
            {
                // waste some processing time...
                float foobar = 3.8523;
                foobar += 34.928;
                foobar *= 3423.928;
                float blob = foobar + 342;
                blob /= 2392.92;;
            }
            l << "TestJob::run(): Our job " << name << " is running :-)" << endl;
    
        }
    }

private:
    Logger& l;
    const string name;
};

int main( int argc, char **argv)
{
    Core& c = *Core::instance();
    
    Logger logger( Logger::NameSqoOssMetric );
    logger.setTeeStream( cout );
  
    logger << "Creating job #1" << endl;
    Job* const j1 = new TestJob( logger, "Job 1" );

    logger << "Creating job #2" << endl;
    Job* const j2 = new TestJob( logger, "Job 2" );

    logger << "Creating job #3" << endl;
    Job* const j3 = new TestJob( logger, "Job 3" );

    logger << "Creating job #4" << endl;
    Job* const j4 = new TestJob( logger, "Job 4" );

    j2->addDependency( j1 );
    j3->addDependency( j1 );
    j4->addDependency( j2 );
    j4->addDependency( j3 );
    
    logger << "Job waiting for execution..." << endl;
   
    c.enqueueJob( j1 );
    c.enqueueJob( j2 );
    c.enqueueJob( j3 );
    c.enqueueJob( j4 );
    
    c.run();
}
