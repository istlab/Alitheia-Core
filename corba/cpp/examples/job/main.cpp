#include <Logger>
#include <Job>
#include <Scheduler>

#include <sstream>
#include <ostream>

#include <boost/thread.hpp>

using namespace std;
using namespace Alitheia;

static boost::mutex mutex;

class TestJob : public Job
{
public:
    TestJob( Logger& l, const string& name )
        : Job(),
          l( l ),
          name( name )
    {
    }
    
    void run()
    {
        for( int i = 0; i < 4; ++i )
        {
            {
                boost::mutex::scoped_lock scoped_lock( mutex );
                l << "TestJob::run(): Our job " << name << " is running :-)" << endl;
            }
            boost::xtime xt;
            boost::xtime_get( &xt, boost::TIME_UTC );
            ++xt.sec;
            boost::thread::sleep( xt );
        }
    }

    void stateChanged( State state )
    {
        boost::mutex::scoped_lock scoped_lock( mutex );
        l << "TestJob::stateChanged(): Our job " << name << " changed to state " << state << endl;
    }

private:
    Logger& l;
    const string name;
};

int main( int argc, char **argv)
{
    Scheduler sched;

    Logger logger( Logger::NameSqoOssMetric );
    logger.setTeeStream( cout );
  
    logger << "Creating job #1" << endl;
    TestJob j1( logger, "Job 1" );

    logger << "Creating job #2" << endl;
    TestJob j2( logger, "Job 2" );

    logger << "Creating job #3" << endl;
    TestJob j3( logger, "Job 3" );

    logger << "Creating job #4" << endl;
    TestJob j4( logger, "Job 4" );

    j2.addDependency( &j1 );
    j3.addDependency( &j1 );
    j4.addDependency( &j2 );
    j4.addDependency( &j3 );
    
    logger << "Job waiting for execution..." << endl;
   
    sched.enqueueJob( &j1 );
    sched.enqueueJob( &j2 );
    sched.enqueueJob( &j3 );
    sched.enqueueJob( &j4 );
    
    j4.waitForFinished();
    boost::mutex::scoped_lock scoped_lock( mutex );
}
