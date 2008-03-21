#ifndef WCMETRICJOB_H
#define WCMETRICJOB_H

#include <Job>
#include <DBObject>

namespace Alitheia
{
    class AbstractMetric;
}

class WcMetricJob : public Alitheia::Job
{
public:
    WcMetricJob( const Alitheia::AbstractMetric* metric, const Alitheia::ProjectFile& file );
    ~WcMetricJob();

    void run();
    void stateChanged( State state );

private:
    const Alitheia::AbstractMetric* const metric;
    Alitheia::ProjectFile projectFile;
};

#endif
