/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2008 - 2010 - Organization for Free and Open Source Software,  
 *                 Athens, Greece.
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

package eu.sqooss.impl.service.admin;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import eu.sqooss.service.admin.ActionParam;
import eu.sqooss.service.admin.AdminAction;
import eu.sqooss.service.admin.AdminActionError;
import eu.sqooss.service.admin.AdminService;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.logging.Logger;

/**
 * The implementation of the system administration service.
 * 
 * @author Georgios Gousios <gousiosg@gmail.com>
 */
public final class AdminServiceImpl extends HttpServlet 
	implements AdminService {
	
	private static final long serialVersionUID = 1L;
	
	/* All the actions we know about, clients can register more */
	private static final String[] actions = {
		"eu.sqooss.impl.service.admin.AssignProjectAction",
		"eu.sqooss.impl.service.admin.ScheduleMetricAction"
	};

	private final String xmlHead = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
	
    /** The actionid/class registry */
    private static HashMap<String, Class<? extends AdminAction>> actionList;
    
    static {
    	actionList = new HashMap<String, Class<? extends AdminAction>>();
    }
    
    /** An reference to a logger*/
    public Logger log;
    
    private BundleContext bc;
    
    public AdminServiceImpl() {}
    
    /** {@inheritDoc} */
    public boolean registerAdminAction(String name, 
            Class<? extends AdminAction> clazz) {
        
        if (actionList.containsKey(name))
            return false;
        
        actionList.put(name, clazz);
        log.info("Registered admin action: " + clazz);
        return true;
    }
    
    /** {@inheritDoc} */
    public AdminAction getAction(String s) {
        Class<? extends AdminAction> a = actionList.get(s);
        if (a == null)
            return null;
        AdminAction aa = null;
        try {
            aa = a.newInstance();
        } catch (InstantiationException e) {
            log.warn("Cannot instantiate action class " + aa + 
                    ". Error: " + e.getMessage());
        } catch (IllegalAccessException e) {
            log.warn("Cannot instantiate action class " + aa + 
                    " Error: " + e.getMessage());
        }
        return aa;
    }
 
    /** {@inheritDoc} */
    public Set<String> getAllActionNames() {
        return actionList.keySet();
    }
    
    public void doGet(HttpServletRequest request, HttpServletResponse response)
    	throws ServletException, IOException {
    	response.getWriter().append(xmlHead);
    	response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("text/xml;charset=UTF-8");
    	Enumeration<String> params = request.getParameterNames();
    	
    	DBService db = AlitheiaCore.getInstance().getDBService();
        db.startDBSession();
    	
        //Get action from URL and try to match it with a known action class
    	String sb = request.getRequestURI();
    	Matcher m = Pattern.compile("/admin/(.*)?").matcher(sb.subSequence(0, sb.length()));
    	if (m.matches()) {
    		String action = m.group(1);
    		try {
				Class<? extends AdminAction> c = actionList.get(action);
				AdminAction aa = c.newInstance();
				Map<ActionParam, Object> actionParams = new HashMap<ActionParam, Object>();
				
                while (params.hasMoreElements()) {
                    String param = params.nextElement();
                    ActionParam ap = ActionParam.valueOf(param);
                    
                    if (ap == null) {
                        log.warn("Unknown parameter " + param);
                        continue;
                    }
                    
                    actionParams.put(ap, request.getParameter(param));
                }
                
                if (aa.execute(actionParams)) {
                    response.getWriter().append(aa.getResult());
                } else {
                    log.warn("Error executing action:" +
                            aa + ":" + aa.getError().toString());
                    response.getWriter().append(
                           aa.getError().toXML());
                }
				
			} catch (InstantiationException e) {
				log.warn("Error instantiating action handler for action:" +
						action + ":" + e.getMessage());
				response.getWriter().append(
						AdminActionError.EMISACTION.toXML());
			} catch (IllegalAccessException e) {
				log.warn("Error instantiating action handler for action:" +
						action + ":" + e.getMessage());
				response.getWriter().append(
						AdminActionError.EMISACTION.toXML());
			} finally {
				if (db.isDBSessionActive()) {
					db.rollbackDBSession();
				}
			}
    	} else {
    		response.getWriter().append(AdminActionError.EMISACTION.toXML());
    	}
    	response.getWriter().flush();
    	db.commitDBSession();
    }
    
    public void doPost(HttpServletRequest request, HttpServletResponse response)
    	throws ServletException, IOException {
    	doGet(request, response);
    }

	@Override
	public void shutDown() {	
	}
	
	@Override
	public boolean startUp() {
		
        /* Get a reference to the HTTP service */
        ServiceReference serviceRef = bc.getServiceReference("org.osgi.service.http.HttpService");
        
        if (serviceRef != null) {
        	HttpService httpService = (HttpService) bc.getService(serviceRef);
            try {
				httpService.registerServlet("/admin", (Servlet) this, null, null);
			} catch (ServletException e) {
				e.printStackTrace();
			} catch (NamespaceException e) {
				e.printStackTrace();
			}
        } else {
            log.error("Could not load the HTTP service.");
        }

        log.info("Succesfully loaded the admin action service");
		
		for (String action : actions) {
			try {
				Class<?> c = Class.forName(action);
				AdminAction aa = (AdminAction) c.newInstance();
				aa.registerWith(this);
				
			} catch (ClassNotFoundException e1) {
				log.error("Error registering action class: " + action + 
						", error was: " + e1.getMessage());
				return false;
			} catch (InstantiationException ie) {
				log.error("Error registering action class: " + action + 
						", error was: " + ie.getMessage());
				return false;
			} catch (IllegalAccessException iae) {
				log.error("Error registering action class: " + action + 
						", error was: " + iae.getMessage());
				return false;
			}
		}
		return true;
	}

	@Override
	public void setInitParams(BundleContext bc, Logger l) {
		this.bc = bc;
		this.log = l;
	}
}
