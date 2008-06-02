#!/usr/bin/python

from alitheia import Logger
from alitheia import Core
from alitheia import Job
from alitheia import Scheduler
from alitheia import ProjectFileMetric

class TestJob(Job):
    def run(self):
        print "run!"

l = Logger( "sqooss" )
l.error( "Test" )

j = TestJob()
s = Scheduler()
s.enqueueJob( j )

c = Core()

m = ProjectFileMetric()
c.registerMetric(m)

j.waitForFinished()

Core.shutdown()
