package eu.sqooss.service.abstractmetric;

import java.util.List;

import eu.sqooss.service.db.DAObject;
import eu.sqooss.service.db.MetricType;
import eu.sqooss.service.db.MetricType.Type;
import eu.sqooss.service.metricactivator.MetricActivationException;

public class MetricQueries {
	private MetricQueries(){
		// UTILITY CLASS
	}
	
	public static final String QRY_SYNC_PV = "select pv.id from ProjectVersion pv " +
    		"where pv.project = :project and not exists(" +
    		"	select pvm.projectVersion from ProjectVersionMeasurement pvm " +
    		"	where pvm.projectVersion.id = pv.id and pvm.metric.id = :metric) " +
    		"order by pv.sequence asc";
    
    public static final String QRY_SYNC_PF = "select pf.id " +
    		"from ProjectVersion pv, ProjectFile pf " +
    		"where pf.projectVersion=pv and pv.project = :project " +
    		"and not exists (" +
    		"	select pfm.projectFile " +
    		"	from ProjectFileMeasurement pfm " +
    		"	where pfm.projectFile.id = pf.id " +
    		"	and pfm.metric.id = :metric) " +
    		"	and pf.isDirectory = false)  " +
    		"order by pv.sequence asc";
    
    public static final String QRY_SYNC_PD = "select pf.id " +
		"from ProjectVersion pv, ProjectFile pf " +
		"where pf.projectVersion=pv and pv.project = :project " +
		"and not exists (" +
		"	select pfm.projectFile " +
		"	from ProjectFileMeasurement pfm " +
		"	where pfm.projectFile.id = pf.id " +
		"	and pfm.metric.id = :metric) " +
		"	and pf.isDirectory = true)  " +
		"order by pv.sequence asc";
    
    public static final String QRY_SYNC_MM = "select mm.id " +
    		"from MailMessage mm " +
    		"where mm.list.storedProject = :project " +
    		"and mm.id not in (" +
    		"	select mmm.mail.id " +
    		"	from MailMessageMeasurement mmm " +
    		"	where mmm.metric.id =:metric and mmm.mail.id = mm.id))";
    
    public static final String QRY_SYNC_MT = "select mlt.id " +
    		"from MailingListThread mlt " +
    		"where mlt.list.storedProject = :project " +
    		"and mlt.id not in (" +
    		"	select mltm.thread.id " +
    		"	from MailingListThreadMeasurement mltm " +
    		"	where mltm.metric.id =:metric and mltm.thread.id = mlt.id)";
    
    public static final String QRY_SYNC_DEV = "select d.id " +
    		"from Developer d " +
    		"where d.storedProject = :project";
    
    public static final String QRY_SYNC_NS = "select ns.id " +
            "from NameSpace ns, ProjectVersion pv " +
            "where pv = ns.changeVersion " +
            "and pv.project = :project " +
            "and not exists ( " +
            "   select nsm " + 
            "   from NameSpaceMeasurement nsm " + 
            "   where nsm.metric.id = :metric " +
            "   and nsm.namespace = ns) " +
            "order by pv.sequence asc";
    
    public static final String QRY_SYNC_ENCUNT = "select encu.id " +
            "from EncapsulationUnit encu, ProjectVersion pv, ProjectFile pf " +
            " where pf.projectVersion = pv " +
            " and encu.file = pf " +
            "and pv.project = :project " +
            "and not exists ( " +
            "    select eum " +
            "    from EncapsulationUnitMeasurement eum " +
            "    where eum.encapsulationUnit = encu " +
            "    and eum.metric.id = :metric " +
            " ) order by pv.sequence asc ";
    
    public static final String QRY_SYNC_EXECUNT = "select exu.id " +
    		"from ExecutionUnit exu, EncapsulationUnit encu, " +
    		"     ProjectVersion pv, ProjectFile pf " +
            "where pf.projectVersion = pv " +
            "and encu.file = pf " +
            "and pv.project = :project " +
            "and exu.changed = true " +
            "and exu.encapsulationUnit = encu " +
            "and not exists ( " +
            "    select eum  " +
            "    from ExecutionUnitMeasurement eum " +
            "    where eum.executionUnit = exu " +
            "    and eum.metric.id = :metric) " +
            "order by pv.sequence asc";
    
    public static String getQuery( Class<? extends DAObject> at ) 
    		throws MetricActivationException {
    	String q = null;
	    if (MetricType.fromActivator(at) == Type.PROJECT_VERSION) {
	    	q = QRY_SYNC_PV;
	    } else if (MetricType.fromActivator(at) == Type.SOURCE_FILE) {
	    	q = QRY_SYNC_PF;
	    } else if (MetricType.fromActivator(at) == Type.SOURCE_DIRECTORY) {
	    	q = QRY_SYNC_PD;
	    } else if (MetricType.fromActivator(at) == Type.MAILING_LIST) {
	    	throw new MetricActivationException("Metric synchronisation with MAILING_LIST objects not implemented");
	    } else if (MetricType.fromActivator(at) == Type.MAILMESSAGE) {
	    	q = QRY_SYNC_MM;
	    } else if (MetricType.fromActivator(at) == Type.MAILTHREAD) {
	    	q = QRY_SYNC_MT;
	    } else if (MetricType.fromActivator(at) == Type.BUG) {
	    	throw new MetricActivationException("Metric synchronisation with BUG objects not implemented");
	    } else if (MetricType.fromActivator(at) == Type.DEVELOPER) {
	    	q = QRY_SYNC_DEV;
	    } else if (MetricType.fromActivator(at) == Type.NAMESPACE) {
            q = QRY_SYNC_NS;
        } else if (MetricType.fromActivator(at) == Type.ENCAPSUNIT) {
            q = QRY_SYNC_ENCUNT;
        } else if (MetricType.fromActivator(at) == Type.EXECUNIT) {
            q = QRY_SYNC_EXECUNT;
        } else {
        	throw new MetricActivationException("Metric synchronisation with GENERIC objects not implemented");
	    }
	    return q;
    }
}
