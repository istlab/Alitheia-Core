#ifndef WRAPPERMETRIC_H
#define WRAPPERMETRIC_H

#include <Metric>
#include <Database>
#include <Logger>
#include <FDS>

#include <string>
#include <vector>

template< typename T>
T join( const std::vector< T >& v, const T& t = T() )
{
    T result;
    if( v.empty() )
        return result;
    for( typename std::vector< T >::const_iterator it = v.begin(); it != v.end() - 1; ++it )
    {
        result += *it;
        result += t;
    }
    result += v.back();

    return result;
}

template< typename T, typename C>
T join( const std::vector< T >& v, const C& c )
{
    return join( v, T( c ) );
}

class ProjectFileWrapperMetric : public Alitheia::ProjectFileMetric
{
public:
    ProjectFileWrapperMetric( const std::string& metric, const std::string& resultType, const std::string& program, 
                              const std::vector< std::string >& arguments );

    bool install();
    std::string name() const;
    std::string author() const;
    std::string description() const;
    std::string version() const;
    std::string result() const;
    std::vector< Alitheia::ResultEntry > getResult( const Alitheia::ProjectFile&, const Alitheia::Metric& metric ) const;
    void run( Alitheia::ProjectFile& );

private:
    Alitheia::Logger logger;
    Alitheia::Database db;
    
    std::string resultType;
    std::string metric;
    std::string program;
    std::vector< std::string > arguments;
};

class ProjectVersionWrapperMetric : public Alitheia::ProjectVersionMetric
{
public:
    ProjectVersionWrapperMetric( const std::string& metric, const std::string& resultType, const std::string& program, 
                                 const std::vector< std::string >& arguments );

    bool install();
    std::string name() const;
    std::string author() const;
    std::string description() const;
    std::string version() const;
    std::string result() const;
    std::vector< Alitheia::ResultEntry > getResult( const Alitheia::ProjectVersion&, const Alitheia::Metric& metric ) const;
    void run( Alitheia::ProjectVersion& );


private:
    Alitheia::FDS fds;
    Alitheia::Logger logger;
    Alitheia::Database db;

    std::string resultType;
    std::string metric;
    std::string program;
    std::vector< std::string > arguments;
};

#endif
