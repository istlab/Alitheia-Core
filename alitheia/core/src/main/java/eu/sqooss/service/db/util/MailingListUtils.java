package eu.sqooss.service.db.util;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.MailMessage;
import eu.sqooss.service.db.MailingList;
import eu.sqooss.service.db.MailingListThread;
import eu.sqooss.service.db.StoredProject;

public class MailingListUtils {
	private DBService dbs;
	
	public MailingListUtils(DBService db) {
		this.dbs = db;
	}

	/**
	 * Get messages in this mailing list whose arrival date
	 * is newer that the provided date.
	 * 
	 * @param d The date to compare the arrival date with
	 * @return A list of messages newer than <tt>d</tt>
	 */
	public List<MailMessage> getMessagesNewerThan(MailingList ml, Date d) {
	
	    String paramDate = "paramDate";
	    String paramMailingList = "paramML";
	    
	    String query =  " select mm " +
	        " from MailMessage mm, MailingList ml " +
	        " where mm.list = ml " +
	        " and mm.list = :" + paramMailingList +
	        " and mm.sendDate > :" + paramDate;
	    
	    Map<String,Object> params = new HashMap<>();
	    params.put(paramDate, d);
	    params.put(paramMailingList, ml);
	    
	    @SuppressWarnings("unchecked")
		List<MailMessage> msgs = (List<MailMessage>) dbs.doHQL(query, params);
	    
	    if (msgs == null || msgs.size() == 0)
	        return Collections.emptyList();
	        
	    return msgs;
	}

	/**
	 * Get the latest mail message in this mailing list.
	 */
	public MailMessage getLatestEmail(MailingList ml) {
	
	    String paramMailingList = "paramML";
	    
	    String query =  " select mm " +
	        " from MailMessage mm, MailingList ml " +
	        " where mm.list = ml " +
	        " and mm.list = :" + paramMailingList +
	        " order by mm.sendDate desc";
	    
	    Map<String,Object> params = new HashMap<>();
	    params.put(paramMailingList, ml);
	    
	    
	    @SuppressWarnings("unchecked")
		List<MailMessage> mml = (List<MailMessage>) dbs.doHQL(query, params, 1);
	    
	    if (mml.isEmpty())
	        return null;
	    
	    return mml.get(0); 
	}

	/**
	 * Get the latest updated thread in this mailing list.
	 */
	public MailingListThread getLatestThread(MailingList ml) {
	
	    String paramMailingList = "paramML";
	    
	    String query =  " select mt " +
	        " from MailThread mt, MailingList ml " +
	        " where mt.list = ml " +
	        " and mm.list = :" + paramMailingList +
	        " order by mt.lastUpdated desc";
	    
	    Map<String,Object> params = new HashMap<>();
	    params.put(paramMailingList, ml);
	    
	    @SuppressWarnings("unchecked")
		List<MailingListThread> mml = (List<MailingListThread>) dbs.doHQL(query, params, 1);
	    
	    if (mml.isEmpty())
	        return null;
	    
	    return mml.get(0); 
	}

	/**
	 * Get the email that kickstarted this thread.
	 */
	public MailMessage getStartingEmail(MailingListThread mlt) {
	
	
	    Map<String, Object> params = new HashMap<>();
	    params.put("thread", mlt);
	    params.put("depth", 0);
	    
	    List<MailMessage> mml = dbs.findObjectsByProperties(MailMessage.class,
	            params);
	
	    if (!mml.isEmpty())
	        return mml.get(0);
	
	    return null;
	}

	/**
	 * Get all messages in this thread by order of arrival.
	 * 
	 * @return The last MailMessage in a thread.
	 */
	public List<MailMessage> getMessagesByArrivalOrder(MailingListThread mlt) {
	
	    String paramThread = "paramThread";
	    
	    String query = "select mm " +
	            " from MailMessage mm, MailingListThread mt " +
	            " where mm.thread = mt " +
	            " and mt = :" + paramThread + 
	            " order by mm.sendDate asc" ;
	    Map<String,Object> params = new HashMap<>(1);
	    params.put(paramThread, mlt);
	    
	    @SuppressWarnings("unchecked")
		List<MailMessage> mm = (List<MailMessage>) dbs.doHQL(query, params);
	    
	    if (mm == null || mm.isEmpty())
	        return Collections.emptyList();
	    
	    return mm;
	}

	/**
	 * Get the number of levels in the reply tree.
	 */
	public int getThreadDepth(MailingListThread mlt) {
	    
	
	    String paramThread = "paramThread";
	    
	    String query = "select max(mm.depth) " +
	            " from MailMessage mm, MailingListThread mt " +
	            " where mt = :" + paramThread +
	            " and mm.thread = mt";
	    Map<String,Object> params = new HashMap<>(1);
	    params.put(paramThread, mlt);
	    
	    @SuppressWarnings("unchecked")
		List<Integer> mm = (List<Integer>) dbs.doHQL(query, params, 1);
	    
	    if (mm == null || mm.isEmpty())
	        return 0;
	    
	    return mm.get(0).intValue();
	}

	/**
	 * Get all emails at the provided depth, ordered by arrival time
	 * @param level The thread depth level for which to select emails.
	 * @return The emails at the specified thread depth.
	 */
	public List<MailMessage> getMessagesAtLevel(MailingListThread mlt, int level) {
	    
	
	    String paramThread = "paramThread";
	    String paramDepth = "paramDepth";
	    
	    String query = "select mm " +
	            " from MailMessage mm, MailingListThread mlt " +
	            " where mm.thread = mlt" +
	            " and mlt = :" + paramThread + 
	            " and mm.depth = :" + paramDepth +
	            " order by mm.sendDate asc";
	    
	    Map<String,Object> params = new HashMap<>(1);
	    params.put(paramThread, mlt);
	    params.put(paramDepth, level);
	    
	    @SuppressWarnings("unchecked")
		List<MailMessage> mm = (List<MailMessage>) dbs.doHQL(query, params);
	    
	    if (mm == null || mm.isEmpty())
	        return Collections.emptyList();
	    
	    return mm;
	}

	/**
	 * Return a stored mail message based on messageId
	 */
	public MailMessage getMessageById(String messageId) {
		Map<String,Object> properties = new HashMap<>(1);
		properties.put("messageId", messageId);
		List<MailMessage> msgList = dbs.findObjectsByProperties(MailMessage.class, properties);
		
		if ((msgList == null) || (msgList.isEmpty())) {
		    return null;
		}
		
		return msgList.get(0);
	}

	/**
	 * Return a stored mail message based on filename
	 */
	public MailMessage getMessageByFileName(String filename) {
	    Map<String,Object> properties = new HashMap<>(1);
	    properties.put("fileName", filename);
	    List<MailMessage> msgList = dbs.findObjectsByProperties(MailMessage.class, properties);
	    
	    if ((msgList == null) || (msgList.isEmpty())) {
	        return null;
	    }
	    
	    return msgList.get(0);
	}

	/**
	 * Get the latest known mail message for the provided project, or null.  
	 */
	public MailMessage getLatestMailMessage(StoredProject sp) {
	    String paramStoredProject = "paramStoredProject";
	
	    String query = "select mm " 
	            + " from MailMessage mm, MailingList ml "
	            + " where mm.list = ml " 
	            + " and ml.storedProject = :" + paramStoredProject 
	            + " order by mm.sendDate desc";
	
	    Map<String, Object> params = new HashMap<>();
	    params.put(paramStoredProject, sp);
	
	    @SuppressWarnings("unchecked")
		List<MailMessage> mm = (List<MailMessage>) dbs.doHQL(query, params, 1);
	
	    if (!mm.isEmpty())
	        return mm.get(0);
	
	    return null;
	}

}
