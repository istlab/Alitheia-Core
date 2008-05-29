from omniORB import CORBA
from threading import Thread

import CosNaming
import Alitheia_idl
import eu
import eu__POA

class CorbaHandler:

    orb = None
    poa = None
    poaobj = None
    orb_thread = None
    m_instance = None

    class OrbThread(Thread):
        orb = None

        def __init__(self,orb):
            Thread.__init__(self)
            self.orb = orb
            self.start()

        def run(self):
            self.orb.run()

    def __init__(self):
        self.orb = CORBA.ORB_init(['-ORBInitRef','NameService=corbaloc:iiop:1.2@localhost:2809/NameService'], CORBA.ORB_ID)
        self.poaobj = self.orb.resolve_initial_references("RootPOA")
        poaManager = self.poaobj._get_the_POAManager()
        poaManager.activate()
        self.orb_thread = CorbaHandler.OrbThread(self.orb)

    @staticmethod
    def instance():
        if CorbaHandler.m_instance is None:
            CorbaHandler.m_instance = CorbaHandler()
        return CorbaHandler.m_instance

    def getObject(self,name):
        nameService = self.orb.resolve_initial_references( "NameService" )
        nameService = nameService._narrow( CosNaming.NamingContext )
        if nameService is None:
            print "Error: Could not find naming service"
            return None
        cosName = [ CosNaming.NameComponent( name, "" ) ]
        obj = nameService.resolve( cosName )
        return obj

    def exportObject(self,obj,name):
        nameService = self.orb.resolve_initial_references( "NameService" )
        nameService = nameService._narrow( CosNaming.NamingContext )
        if nameService is None:
            print "Error: Could not find naming service"
            return None
        cosName = [ CosNaming.NameComponent( name, "" ) ]
        nameService.rebind(cosName, obj._this())

    def shutdown(self):
        self.orb.shutdown(True)

class Job (eu__POA.sqooss.impl.service.corba.alitheia.Job):
    name = ""

    def run(self):
        print "run!"

    def priority(self):
        print "priority!"
        return 0

    def setState(self,state):
        print "stateChanged!"
        print state
        return

class Scheduler:
    scheduler = None
    
    def __init__(self):
        self.scheduler = CorbaHandler.instance().getObject( "AlitheiaScheduler" )

    def enqueueJob(self,job):
        if len(job.name) == 0:
            self.registerJob(job)
        self.scheduler.enqueueJob(job.name)
    
    def registerJob(self,job):
        job.name = "Alitheia_Job_" + str(Core.instance().getUniqueId())
        CorbaHandler.instance().exportObject(job, job.name)
        self.scheduler.registerJob(job.name)

class Logger:
    logger = None
    name = None

    def __init__( self, name ):
        self.logger = CorbaHandler.instance().getObject( "AlitheiaLogger" )
        self.name = name

    def debug( self, message ):
        self.logger.debug( self.name, message )

    def info( self, message ):
        self.logger.info( self.name, message )

    def warn( self, message ):
        self.logger.warn( self.name, message )

    def error( self, message ):
        self.logger.error( self.name, message )

class Core:
    core = None
    m_instance = None

    def __init__(self):
        self.core = CorbaHandler.instance().getObject( "AlitheiaCore" )

    @staticmethod
    def instance():
        if Core.m_instance is None:
            Core.m_instance = Core()
        return Core.m_instance

    @staticmethod
    def shutdown():
        CorbaHandler.instance().shutdown();

    def getUniqueId(self):
        return self.core.getUniqueId()
