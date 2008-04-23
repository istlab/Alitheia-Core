#ifndef WRAPPERMETRICJOB_H
#define WRAPPERMETRICJOB_H

#include <Job>
#include <DBObject>

#include <string>
#include <vector>

#include "wrappermetric.h"

class ProjectFileWrapperMetricJob : public Alitheia::Job
{
public:
    ProjectFileWrapperMetricJob( const ProjectFileWrapperMetric* metric, const std::string& program,
                                 const std::vector< std::string >& arguments, const Alitheia::ProjectFile& file );
    ~ProjectFileWrapperMetricJob();

    void run();
    void stateChanged( State state );

private:
    const ProjectFileWrapperMetric* const metric;
    Alitheia::ProjectFile projectFile;
    const std::string program;
    const std::vector< std::string > arguments;
};

class ProjectVersionWrapperMetricJob : public Alitheia::Job
{
public:
    ProjectVersionWrapperMetricJob( const ProjectVersionWrapperMetric* metric, const std::string& program,
                                    const std::vector< std::string >& arguments, const Alitheia::ProjectVersion& version );
    ~ProjectVersionWrapperMetricJob();

    void run();
    void stateChanged( State state );

private:
    const ProjectVersionWrapperMetric* const metric;
    Alitheia::ProjectVersion projectVersion;
    const std::string program;
    const std::vector< std::string > arguments;
};

#endif
