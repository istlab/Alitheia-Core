#!/usr/bin/python

from alitheia import Logger
from alitheia import Core
from alitheia import Job
from alitheia import Scheduler
from alitheia import ProjectFileMetric
from alitheia import StoredProject
from alitheia import Database
from alitheia import FDS


class TestJob(Job):
    def run(self):
        print 'run!'

class Metric(ProjectFileMetric):
    def run(self,file):
        print file

    def name(self):
        return 'TestMetric'

    def install(self):
        return True

    def remove(self):
        return True

    def version(self):
        return '1.0.0.0'

#l = Logger('sqooss')
#l.error('Test')

#j = TestJob()
#s = Scheduler()
#s.enqueueJob( j )

#c = Core()

#m = Metric()
#c.registerMetric(m)

#j.waitForFinished()

db = Database()
props = {'id': 2232}
props = {'name': 'SVN'}
objects = db.findObjectsByProperties(StoredProject,props)
print objects

version = StoredProject.getLastProjectVersion(StoredProject.getProjectByName('SVN'))
print version, version.version

fds = FDS()
co = fds.getCheckout(version)
print co

for file in co.files:
    print file.getFileName()
    for line in file:
        print line

Core.shutdown()
