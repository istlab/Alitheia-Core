from omniORB import CORBA
import CosNaming
import Alitheia_idl
import eu

from corbahandler import CorbaHandler

class Logger:
    logger = None
    name = None

    def __init__( self, name ):
        self.logger = CorbaHandler.instance().getObject( "Logger" )
        self.name = name

    def debug( self, message ):
        self.logger.debug( self.name, message )

    def info( self, message ):
        self.logger.info( self.name, message )

    def warn( self, message ):
        self.logger.warn( self.name, message )

    def error( self, message ):
        self.logger.error( self.name, message )
