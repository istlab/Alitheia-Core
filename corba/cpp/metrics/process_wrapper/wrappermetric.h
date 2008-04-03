#ifndef WRAPPERMETRIC_H
#define WRAPPERMETRIC_H

#include <Metric>
#include <Logger>

#include <QStringList>

class ProjectFileWrapperMetric : public Alitheia::ProjectFileMetric
{
public:
    ProjectFileWrapperMetric();

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

    QString program;
    QStringList arguments;
};

#endif
