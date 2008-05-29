#!/usr/bin/python

from alitheia import Logger
from alitheia import Core
from alitheia import Job
from alitheia import Scheduler

l = Logger( "sqooss" )
l.error( "Test" )

j = Job()
s = Scheduler()
s.enqueueJob( j )
