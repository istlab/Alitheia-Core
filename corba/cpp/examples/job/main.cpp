#include <Logger>
#include <Core>
#include <Job>

#include <sstream>
#include <ostream>

#include <boost/thread.hpp>

using namespace std;
using namespace Alitheia;

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
        if( state == Finished )
        {
            l << "Deleting job..." << std::endl;
            try{
                Logger log( Logger::NameSqoOssMetric );
                log.setTeeStream( std::cout );
                log << "deleting..." << std::endl;
            delete this;
                log << "deleting done." << std::endl;
            } catch( ... )
            {
            }
            return;
            l << "Done deleting job..." << std::endl;
        }
        l << "TestJob::stateChanged(): Our job " << name << " changed to state " << state << endl;
    }

private:
    Logger& l;
    const string name;

    static boost::mutex mutex;
};

boost::mutex TestJob::mutex;

#include <FDS>
int main( int argc, char **argv)
{
    Core& c = *Core::instance();
   
    FDS fds;  

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
    
    j4->waitForFinished();
}
