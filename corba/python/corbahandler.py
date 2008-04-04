#!/usr/bin/python

from omniORB import CORBA
import CosNaming
import Alitheia_idl
import eu

class CorbaHandler:

    orb = None
    poaobj = None

    m_instance = None

    def __init__(self):
        self.orb = CORBA.ORB_init(['-ORBInitRef','NameService=corbaloc:iiop:1.2@localhost:2809/NameService'], CORBA.ORB_ID)
        self.poaobj = self.orb.resolve_initial_references("RootPOA")

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
