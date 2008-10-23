#!/usr/bin/python

from alitheia import Logger
from alitheia import Core
from alitheia import Job
from alitheia import Scheduler
from alitheia import ProjectFileMetric
from alitheia import StoredProject
from alitheia import ProjectVersion
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
props = {'name': 'Boost'}
objects = db.findObjectsByProperties(StoredProject, props)
print objects

print StoredProject.getProjectByName('Boost')

version = StoredProject.getLastProjectVersion(StoredProject.getProjectByName('Boost'))
print version, version.version

fds = FDS()
co = fds.getCheckout(version)

for file in co.files:
    print file.getFileName()

for line in co.files[0]:
    print line

Core.shutdown()
