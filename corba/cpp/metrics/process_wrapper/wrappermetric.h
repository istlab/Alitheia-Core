#ifndef WRAPPERMETRIC_H
#define WRAPPERMETRIC_H

#include <Metric>
#include <Logger>

#include <string>
#include <vector>

class ProjectFileWrapperMetric : public Alitheia::ProjectFileMetric
{
public:
    ProjectFileWrapperMetric( const std::string& program, const std::vector< std::string >& arguments );

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

    std::string program;
    std::vector< std::string > arguments;
};

class ProjectVersionWrapperMetric : public Alitheia::ProjectVersionMetric
{
public:
    ProjectVersionWrapperMetric( const std::string& program, const std::vector< std::string >& arguments );

    bool install();
    std::string name() const;
    std::string author() const;
    std::string description() const;
    std::string version() const;
    std::string result() const;
    std::string getResult( const Alitheia::ProjectVersion& ) const;
    void run( Alitheia::ProjectVersion& );

private:
    Alitheia::Logger logger;

    std::string program;
    std::vector< std::string > arguments;
};

#endif
