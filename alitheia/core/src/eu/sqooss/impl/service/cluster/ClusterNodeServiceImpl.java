/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2008 Athens University of Economics and Business
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *
 *     * Redistributions in binary form must reproduce the above
 *       copyright notice, this list of conditions and the following
 *       disclaimer in the documentation and/or other materials provided
 *       with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package eu.sqooss.impl.service.cluster;

import java.io.IOException;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.List;


import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.cluster.ClusterNodeActionException;
import eu.sqooss.service.cluster.ClusterNodeService;
import eu.sqooss.service.db.ClusterNode;
import eu.sqooss.service.db.ClusterNodeProject;

import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;

import eu.sqooss.service.updater.UpdaterService;
import eu.sqooss.service.updater.UpdaterService.UpdateTarget;

/**
 * @author George M. Zouganelis
 *
 */
public class ClusterNodeServiceImpl extends HttpServlet implements EventHandler, ClusterNodeService {
    private static final long serialVersionUID = 1L;
	static final String localServerName;
	static{
		String hostname;
		try {
			java.net.InetAddress localMachine = java.net.InetAddress.getLocalHost();
			hostname = localMachine.getHostName();
		}
		catch(java.net.UnknownHostException ex) {
    		// TODO: Clustering - Implement a hashing algorithm for unique server identification
			hostname = "unknown host";
		} 		
		localServerName = hostname;
			
	}

    private Logger logger = null;
    private AlitheiaCore core = null;
    private HttpService httpService = null;
    private BundleContext context;
    private DBService dbs = null;
    private UpdaterService upds = null;
    
    private ClusterNode thisNode = null;

    public ClusterNodeServiceImpl(BundleContext bc, Logger logger) throws ServletException,
            NamespaceException {
        this.context = bc;
        this.logger = logger;
        /* Get a reference to the core service*/
        ServiceReference serviceRef = null;
        serviceRef = context.getServiceReference(AlitheiaCore.class.getName());
        core = (AlitheiaCore) context.getService(serviceRef);
        dbs = core.getDBService();
        upds = core.getUpdater();
        if (logger != null) {
            logger.info("Got a valid reference to the logger");
        } else {
            System.out.println("ERROR: ClusteNodeService got no logger");
        }

        /* Get a reference to the HTTP service */
        serviceRef = context.getServiceReference("org.osgi.service.http.HttpService");
        if (serviceRef != null) {
            httpService = (HttpService) context.getService(serviceRef);
           	httpService.registerServlet("/clusternode", (Servlet) this, null, null);
        } else {
            logger.error("Could not load the HTTP service.");
        }
            
        Dictionary<String, String> props = new Hashtable<String, String>(1);
        props.put(EventConstants.EVENT_TOPIC, DBService.EVENT_STARTED);
        bc.registerService(EventHandler.class.getName(), this, props);
       
        
        logger.info("Succesfully started clusternode service");

    }
    
    public void handleEvent(Event e) {
        if (DBService.EVENT_STARTED.equals(e.getTopic())) {
           // At this point, this ClusterNode has not been registered to the database yet, so do it!
     	   if (thisNode == null) { // paranoia check
     	       dbs.startDBSession();
     	       // Check if previously registered in DB
     	       Map<String, Object> serverProps = new HashMap<String, Object>(1);
     	       serverProps.put("name", localServerName);        
     	       List<ClusterNode> s = dbs.findObjectsByProperties(ClusterNode.class, serverProps);
     	       
     	       if (s.isEmpty()) { 
         	       // not registered yet, create a record in DB
     	       	   thisNode = new ClusterNode();
     	       	   thisNode.setName(localServerName);
     	       	   if ( !dbs.addRecord(thisNode) ){
     	       	      logger.error("Failed to register ClusterNode <" + localServerName + ">");
                      dbs.rollbackDBSession();
     	       	   } else {
                      dbs.commitDBSession();
     	         	  logger.info("ClusterNode <" + localServerName + "> registered succesfully.");
     	       	   }
     	       } else {
     	    	   // already registered, keep the record from DB
                   dbs.rollbackDBSession();
     	           thisNode = s.get(0);
  	         	   logger.info("ClusterNode <" + localServerName + "> registered succesfully.");
     	       }
     	   }
        }
    }

    public String getClusterNodeName(){
	   return thisNode.getName();
    }
    
    /**
     * Assign a StoredProject to a ClusterNode
     * Reasonable causes of failure:
     *  1.NULL passed server
     *  2.NULL passed project
     *  3.Assignment is locked (server is working on project)
     *  
     * @param node the cluster node target
     * @param project stored project to assign 
     * @return
     */
    public boolean assignProject(ClusterNode node, StoredProject project) throws ClusterNodeActionException {
    	// check if valid server passed
        if (node==null) {
    		throw new ClusterNodeActionException("Request to assign a project to a null clusternode");
    	}
    	// check if valid project passed
    	if (project==null) {
    		throw new ClusterNodeActionException("Request to assign a null project to a clusternode");
    	}

        try {          
        	// check if project is allready assigned to any ClusterNode
            ClusterNodeProject assignment = ClusterNodeProject.getProjectAssignment(project);
            if (assignment == null) {
                // new project assignment
                logger.info("Assigning project " + project.getName() + " to "
                        + node.getName());
                assignment = new ClusterNodeProject();
                assignment.setProject(project);
                assignment.setNode(node);
                assignment.setLocked(false);
                return dbs.addRecord(assignment);
            } else {
                logger.info("Moving project " + project.getName() + " from "
                        + assignment.getNode().getName() + " to "
                        + node.getName());
                if (assignment.getNode().getId() == node.getId()) {
                    logger.info("No need to move " + project.getName()
                            + " - Already assigned!");
                    return true;
                }
                // TODO: Clustering - Find a way to implement a robust Locking
                // mechanism
                // A project shouldn't be moved when there is an Update or a
                // Metric job in process
                // For now, no locking is performed
                if (assignment.isLocked()) {
                    throw new ClusterNodeActionException("Project ["
                            + project.getName() + "] is locked! - aborting");
                }
                assignment.setNode(node);
            }
        } catch (Exception e) {
            throw new ClusterNodeActionException("Failed to assign project ["
                    + project.getName() + "] to clusternode [" + node.getName()
                    + "]");
        }
    	return true;
    }

    /**
     * Assign a StoredProject to this ClusterNode
     * @param project project to assign
     * @return  
     */
    public boolean assignProject(StoredProject project) throws ClusterNodeActionException {
		try {
			return assignProject(thisNode, project);
		} catch (ClusterNodeActionException ex) {
			throw ex;
		}
    }

    /**
     * Overload for convenience. Use string instead of stored project.
     * @param projectname project's name to assign
     */
    public boolean assignProject(String projectname) throws ClusterNodeActionException {
    	dbs.startDBSession();
    	StoredProject project = StoredProject.getProjectByName(projectname);
    	dbs.rollbackDBSession();
        if (project == null) {
            //the project was not found, can't be assign
        	String errorMessage = "The project [" + projectname + "] was not found"; 
            logger.warn(errorMessage);
            throw new ClusterNodeActionException(errorMessage);
        }
        try {
            return assignProject(project);
        } catch (ClusterNodeActionException ex) {
        	throw ex;
        }
        
    }
    
    /**
     * Check if a StoredProject is assigned to this ClusterNode
     * @param project project to check
     * @return  
     */
    public boolean isProjectAssigned(StoredProject project){
        return ClusterNodeProject.isProjectAssigned(this.thisNode, project);
    }

    
    // Format an XML response
    private String createXMLResponse(String resultMessage, String statusMessage, int statusCode){
        StringBuilder s = new StringBuilder();
        s.append("<?xml version=\"1.0\"?>\n");
        s.append("<sqo-oss-response service=\"clusternode\">\n");
        if (resultMessage!=null) {
            s.append("<result>" + resultMessage + "</result>\n");
        } else {
            s.append("<result/>\n");
        }
        s.append("<status code=\"" + String.valueOf(statusCode) + "\"");
        if (statusMessage!=null) {
            s.append(">" + statusMessage + "</status>\n");
        } else {
            s.append("/>\n");
        }
        s.append("</sqo-oss-response>\n");
    	return s.toString();
    }
    
    // send the XML response back to the client
    private void sendXMLResponse(HttpServletResponse response, int status, String content) throws ServletException, IOException {
        response.setStatus(status);
    	response.setContentType("text/xml;charset=UTF-8");
    	response.getWriter().println(content);
    	response.flushBuffer();
    }
    

    /**
     * This is the standard HTTP request handler. It maps GET parameters based on 
     * the mandatory 'action' parameter to misc internal processes.
     *
     * The response codes in HTTP are used as follows:
     * - SC_OK  if the requested action succeeds
     * - SC_BAD_REQUEST (400) if the request is syntactically incorrect, which in
     *          this case means that one of the required parameter "action"
     *          is missing, or projectid is not a long integer.
     * - SC_NOT_FOUND (404) if the project or clusternode does not exist in the database.
     * - SC_NOT_IMPLEMENTED if the action type is not supported
     */
    @SuppressWarnings("unchecked")
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    	
    	String requestedAction = request.getParameter("action");

    	String projectname = request.getParameter("projectname");
        String projectid = request.getParameter("projectid");
        String clusternode = request.getParameter("clusternode");
    	
        String content;          // holder of complete response output
        StringBuilder bcontent;  // holder of complete response output
        
        StoredProject project;
        ClusterNode node;
        
        // ERROR if no action requested
        if (requestedAction == null) {
        	content=createXMLResponse(null, "Unknown action",HttpServletResponse.SC_BAD_REQUEST);
        	sendXMLResponse(response, HttpServletResponse.SC_BAD_REQUEST,content); 
           return;
        }
        
        // ERROR if unknown action requested
        ClusterNodeAction action = null;
        try {
            action = ClusterNodeAction.valueOf(requestedAction.toUpperCase());
        } catch (IllegalArgumentException e) {
            String errorMessage = "Bad action [" + requestedAction + "]";
            logger.warn(errorMessage);
            content=createXMLResponse(null, errorMessage,HttpServletResponse.SC_NOT_IMPLEMENTED);
            sendXMLResponse(response, HttpServletResponse.SC_NOT_IMPLEMENTED, content);
            return;
        }
        
          
        // Perform Actions
        switch (action){
         case ASSIGN_PROJECT :
        	 // valid parameters:
        	 // projectname : Name of the project to assign.  
        	 // projectid   : ID of the project to assign. 
        	 //               Used ONLY if projectname parameter is missing, or projectname not found
        	 // clusternode : The Clusternode name to which the project will be assigned
        	 //               If empty, assign it to this clusternode
        	 // Example: http://localhost:8088/clusternode?action=assign_project&projectname=iTALC&clusternode=sqoserver1

        	 dbs.startDBSession();
         	 project = StoredProject.getProjectByName(projectname);
         	 dbs.rollbackDBSession();
         	 if (project==null) {
         		 if (projectid!=null)  {
                     long id = 0;
                     try {
                    	 id = Long.valueOf(projectid);
                     } catch (Exception ex){
              	    	 content=createXMLResponse(null,"Invalid projectid [" + projectid + "]", HttpServletResponse.SC_BAD_REQUEST);
            	    	 sendXMLResponse(response, HttpServletResponse.SC_BAD_REQUEST, content);
            	    	 break;                   	 
                     }
                     dbs.startDBSession();
            		 project = dbs.findObjectById(StoredProject.class, id);
            		 dbs.rollbackDBSession();
            		 if (project==null) {
               	    	content = createXMLResponse(null,"Project with id:" + projectid + " not found", HttpServletResponse.SC_NOT_FOUND);
            	    	sendXMLResponse(response, HttpServletResponse.SC_NOT_FOUND, content);
            	    	break;                   	             			 
            		 }
         	     } else {
         	    	content = createXMLResponse(null,"Project " + projectname + " not found", HttpServletResponse.SC_NOT_FOUND);
        	    	sendXMLResponse(response, HttpServletResponse.SC_NOT_FOUND, content);
        	    	break;
         	     }
         	 }
         	 
         	 if (clusternode==null) {
         	     node = thisNode;	 
         	 } else {
         	     dbs.startDBSession();
         	     node = ClusterNode.getClusteNodeByName(clusternode);
         	     dbs.rollbackDBSession();
         	     if (node==null) {
                     content = createXMLResponse(null,"ClusterNode " + clusternode + " not found", HttpServletResponse.SC_NOT_FOUND);
                     sendXMLResponse(response, HttpServletResponse.SC_NOT_FOUND, content);
                     break;         	         
         	     }
         	 }
        	 try {
        	     if (assignProject(node,project)){
        	    	content = createXMLResponse(null, "Project " + project.getName() + " assigned to " + node.getName(), HttpServletResponse.SC_OK);
        	    	sendXMLResponse(response, HttpServletResponse.SC_OK, content);       	    	
        	     }        	     
        	 } catch (ClusterNodeActionException ex) {
     	    	content = createXMLResponse(null, ex.getMessage(), HttpServletResponse.SC_BAD_REQUEST);
    	    	sendXMLResponse(response, HttpServletResponse.SC_BAD_REQUEST, content);        		 
        	 }     
        	 break;
         case GET_ASSIGNED_PROJECTS:
        	 // valid parameters:
        	 // clusternode : The Clusternode name to query for
        	 //               If empty, assign it to this clusternode
        	 // Example: http://localhost:8088/clusternode?action=get_assigned_projects&clusternode=sqoserver1

             // TODO: Clustering - Extract interface  
             if (clusternode==null) {
           	     node = thisNode;	 
           	 } else {
           	     dbs.startDBSession();
           	     node = ClusterNode.getClusteNodeByName(clusternode);
           	     dbs.rollbackDBSession();
           	 }
         	 if (node==null){
      	    	content = createXMLResponse(null, "ClusterNode "+clusternode+" not found", HttpServletResponse.SC_NOT_FOUND);
    	    	sendXMLResponse(response, HttpServletResponse.SC_NOT_FOUND, content);
    	    	break;
         	 }
         	          	 
             bcontent = new StringBuilder();
             dbs.startDBSession();
             List<ClusterNodeProject> assignments = ClusterNodeProject.getNodeAssignments(node);
             if ((assignments!=null) &&  (assignments.size()>0) ){
                 bcontent.append("\n");
                 for (ClusterNodeProject cnp : assignments) {                
                     project = cnp.getProject();
                     bcontent.append("<project id=\"" + project.getId() + "\"");
                     
                     // report lock status (currently unused)
                     bcontent.append(" locked=\"");
                     if (cnp.isLocked()) {
                        bcontent.append("yes\"");
                     } else {
                        bcontent.append("no\"");
                     }

                     // check if project is currently being updated
                     // yes/no/unknown, (unknown means that this project is assigned to another clusternode instance)                    
                     bcontent.append(" isUpdating=\"");
                     if (node.getId()==thisNode.getId()) {
                         if (upds.isUpdateRunning(project, UpdateTarget.ALL)) {
                             bcontent.append("yes\"");
                         } else {
                             bcontent.append("no\"");
                         }
                     } else {
                         bcontent.append("unknown\""); 
                     }
                     
                     bcontent.append(">" + project.getName() + "</project>\n");
                 }
             }
             dbs.rollbackDBSession();
             content = createXMLResponse(bcontent.toString(), "Project list processed succesfuly", HttpServletResponse.SC_OK);
             sendXMLResponse(response, HttpServletResponse.SC_OK, content);
        	 break;
         case GET_KNOWN_SERVERS:
             // valid parameters: No need for parameters!
             // Example: http://localhost:8088/clusternode?action=get_known_servers
             bcontent = new StringBuilder();
             dbs.startDBSession();
             List<ClusterNode> nodes = (List<ClusterNode>) dbs.doHQL("FROM ClusterNode",null);
             if ((nodes!=null) &&  (nodes.size()>0) ){
                 bcontent.append("\n");
                 for (ClusterNode cn : nodes) {                
                     bcontent.append("<clusternode id=\"" + cn.getId() + "\">" + cn.getName() + "</clusternode>\n");
                 }
             }
             dbs.rollbackDBSession();
             content = createXMLResponse(bcontent.toString(), "Clusternode list processed succesfuly", HttpServletResponse.SC_OK);
             sendXMLResponse(response, HttpServletResponse.SC_OK, content);
        	 break;
         default:
        	 // you shouldn't be here! - implement missing actions!
        	 
        }
    
        
        
        
    }

    public Object selfTest() {
        return null;
    }
}
