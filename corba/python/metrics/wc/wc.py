#!/usr/bin/python

import sys
sys.path.append('../..')

from alitheia import Core
from alitheia import Database
from alitheia import Logger
from alitheia import MetricType
from alitheia import Job
from alitheia import ProjectFileMetric
from alitheia import ProjectFileMeasurement
from alitheia import Scheduler
from alitheia import SourceCode

import datetime

class WcMetricJob(Job):
    logger = Logger('sqooss.metric')
    metric = None
    projectFile = None

    def __init__(self,metric,projectFile):
        self.metric = metric
        self.projectFile = projectFile

    def run(self):
        self.logger.info(self.metric.name() + ': measuring ' + self.projectFile.getFileName())
        count = 0
        for line in self.projectFile:
            count += 1

        metrics = self.metric.getSupportedMetrics()
        print metrics
        if len(metrics) == 0:
            return

        # add the result
        m = ProjectFileMeasurement()
        m.measureMetric = metrics[0]
        m.file = self.projectFile
        m.whenRun = datetime.datetime.now().isoformat()
        m.result = str(count)

        db = Database()
        db.addRecord(m)

class WcMetric(ProjectFileMetric):
    logger = Logger('sqooss.metric')
    scheduler = Scheduler()

    def run(self,file):
        self.scheduler.enqueueJob(WcMetricJob(self,file))

    def name(self):
        return 'CORBA Wc metric'

    def install(self):
        self.logger.info(self.name() + ': installing')
        return self.addSupportedMetrics(self.description(),'LOC',SourceCode)

    def remove(self):
        return True

    def description(self):
        return 'Line counting metric via CORBA'

    def version(self):
        return '0.0.1'

c = Core()

m = WcMetric()
c.registerMetric(m)
