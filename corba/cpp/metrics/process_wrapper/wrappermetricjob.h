#ifndef WRAPPERMETRICJOB_H
#define WRAPPERMETRICJOB_H

#include <Job>
#include <DBObject>
#include <QObject>
#include <QStringList>
#include <QPointer>

namespace Alitheia
{
    class AbstractMetric;
}

class QProcess;

class ProjectFileWrapperMetricJob : public QObject,
                                    public Alitheia::Job
{
    Q_OBJECT
public:
    ProjectFileWrapperMetricJob( const Alitheia::AbstractMetric* metric, const QString& program,
                                 const QStringList& arguments, const Alitheia::ProjectFile& file );
    ~ProjectFileWrapperMetricJob();

    void run();
    void stateChanged( State state );

private Q_SLOTS:
    void readyReadStandardOutput();

private:
    const Alitheia::AbstractMetric* const metric;
    Alitheia::ProjectFile projectFile;
    const QString program;
    QStringList arguments;
    QPointer< QProcess > process;
    QString result;
};

#endif
