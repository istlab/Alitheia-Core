package paxosk.sqoss;

import paxosk.sqoss.portalproject.dbupdate.PortalProjectInserter;
import paxosk.sqoss.project.dbupdate.*;
import paxosk.sqoss.developer.dbupdate.*;
import paxosk.sqoss.portal.dbupdate.*;
import paxosk.sqoss.mlist.dbupdate.*;
import paxosk.sqoss.mlist.rankupdate.*;
import paxosk.sqoss.svn.*;

public class Main 
{
    public static void main(String[] args)
    {                
//        PortalInserter poi=new PortalInserter();
//        poi.start();
//        ProjectInserter pri=new ProjectInserter();
//        pri.start();        
//        PortalProjectInserter ppi=new PortalProjectInserter();
//        ppi.start();        
//        DeveloperInserter di=new DeveloperInserter();
//        di.start();        
        SVNParser sd=new SVNParser();
        sd.start();
        
        MailInserter mi=new MailInserter();
        mi.start();      
        IncomingReferencesUpdater refUpdater=new IncomingReferencesUpdater(mi.getConnection(),
                                        mi.getLogger(), mi.getProperties());
        refUpdater.exec();
    }
}
