#!/usr/bin/python

from alitheia import Logger
from alitheia import Core
from alitheia import Job
from alitheia import Scheduler
from alitheia import ProjectFileMetric

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

l = Logger('sqooss')
l.error('Test')

j = TestJob()
s = Scheduler()
s.enqueueJob( j )

c = Core()

m = Metric()
c.registerMetric(m)

j.waitForFinished()

#Core.shutdown()
