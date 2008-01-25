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
    TestJob( Logger& l )
        : l( l )
    {
    }
    
    void run()
    {
        l << "TestJob::run(): Our job is running :-)" << endl;
    }

private:
    Logger& l;
};

int main( int argc, char **argv)
{
    Core c;
    
    Logger logger( Logger::NameSqoOssMetric );
    logger.setTeeStream( cout );
    
    Job* const j = new TestJob( logger );
    logger << "Registering C++ client job..." << endl;
    
    const int id = c.registerJob( "MyCorbaJob", j );
    logger << "C++ client job registered, id is " << id << "." << endl;
    logger << "Job waiting for execution..." << endl;
    
    c.run();
}
