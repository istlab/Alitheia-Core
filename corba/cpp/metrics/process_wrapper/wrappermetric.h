#ifndef WRAPPERMETRIC_H
#define WRAPPERMETRIC_H

#include <Metric>
#include <Logger>
#include <FDS>

#include <string>
#include <vector>

class ProjectFileWrapperMetric : public Alitheia::ProjectFileMetric
{
public:
    ProjectFileWrapperMetric( const std::string& metric, const std::string& program, 
                              const std::vector< std::string >& arguments );

    bool install();
    std::string name() const;
    std::string author() const;
    std::string description() const;
    std::string version() const;
    std::string result() const;
    std::string getResult( const Alitheia::ProjectFile& ) const;
    void run( Alitheia::ProjectFile& );

private:
    Alitheia::Logger logger;

    std::string metric;
    std::string program;
    std::vector< std::string > arguments;
};

class ProjectVersionWrapperMetric : public Alitheia::ProjectVersionMetric
{
public:
    ProjectVersionWrapperMetric( const std::string& metric, const std::string& program, 
                                 const std::vector< std::string >& arguments );

    bool install();
    std::string name() const;
    std::string author() const;
    std::string description() const;
    std::string version() const;
    std::string result() const;
    std::string getResult( const Alitheia::ProjectVersion& ) const;
    void run( Alitheia::ProjectVersion& );

    Alitheia::FDS fds;

private:
    Alitheia::Logger logger;

    std::string metric;
    std::string program;
    std::vector< std::string > arguments;
};

#endif
