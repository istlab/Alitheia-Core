package eu.sqooss.impl.service.webadmin.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import com.google.common.html.HtmlEscapers;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.impl.service.webadmin.ITranslation;
import eu.sqooss.impl.service.webadmin.Translation;
import eu.sqooss.impl.service.webadmin.servlets.exceptions.PageNotFoundException;
import eu.sqooss.impl.service.webadmin.templates.NullTool;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.logging.LogManager;
import eu.sqooss.service.logging.Logger;

/**
 * Provides a base class for all webadmin servlets
 * Contains the logic to process HTTPRequest and pass them off to the concrete servlet render() method
 * It also contains some common functionality and dependencies
 */
@SuppressWarnings("serial")
public abstract class AbstractWebadminServlet extends HttpServlet implements IWebadminServlet {

	protected static final String TEMPLATE_ROOT = "/webadmin";

	private static final String TEMPLATE_MSG = "/message.vm";

	private final VelocityEngine ve;

	// Services that most likely all subclasses will use
	protected final DBService sobjDB;
	protected final Logger sobjLogger;

	public AbstractWebadminServlet(VelocityEngine ve, AlitheiaCore core) {
		try {
			LogManager logManager = core.getLogManager();
			sobjLogger = logManager.createLogger(Logger.NAME_SQOOSS_WEBADMIN);
		} catch(NullPointerException e) {
			// We can't get a logger, this is going great...
			// Implementing null checks everywhere is a PIA,
			// so we just as well might die right here right now then die to a NullPointerException along the way
			throw new RuntimeException("Could not retrieve a logger for the webadmin service (template " + this.getClass() + ")");
		}

		sobjDB = core.getDBService();
		if(sobjDB == null && sobjLogger != null) {
			sobjLogger.error("Could not get the database component's instance.");
		}

		this.ve = ve;
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse response)
			throws ServletException, IOException {
		preRender();


		/**
		 * You might (rightly) consider making this a field
		 * However: note that this might make for easier testing and debugging and prevent possible interference
		 * Also: possible race conditions if a servlet 2 identical URL's are proccesed at the same time (I do not know if this really is a risk, but check this)
		 */
		VelocityContext vc = createDefaultVC(req);
		Template t = null;
		try {
			t = render(req, vc);
		}
		// "Normal" exceptions
		catch(PageNotFoundException e) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			t = makeErrorMsg(vc, "Page not found", "/");
		}

		if(t == null) {
			String request = req.getRequestURL() + (req.getQueryString()==null?"":"?" + req.getQueryString());
			getLogger().warn("Servlet " + this.getClass().getSimpleName() + " failed rendering request " + request);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			t = makeErrorMsg(vc, "Internal error while processing the request");
			if(t == null)
				throw new ServletException("Failed rendering the request and I couldn't even make an error message. Sorry about that.\nThis request caused me to break:\n" + HtmlEscapers.htmlEscaper().escape(request));
		}

		if(t != null){
			response.setContentType("text/html");
			t.merge(vc, response.getWriter());
		}

		postRender();

	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doGet(req,resp);
	}

	/**
	 * Create the default VelocityContext with variables that every template uses or might use
	 */
	private VelocityContext createDefaultVC(HttpServletRequest req) {
		VelocityContext vc = new VelocityContext();
		// Add the translation class to every page
		vc.put("tr", getTranslation());
		// Add the current path
		vc.put("path", req.getRequestURI());
		// Add the NullTool and ListTool
		vc.put("null", new NullTool());

		return vc;
	}

	/**
	 * Sets up neccesary conditions before rendering (like starting a DB serssion)
	 */
	private void preRender() {
		if (!getDB().isDBSessionActive()) {
			getDB().startDBSession();
		}
	}

	protected abstract Template render(HttpServletRequest req, VelocityContext vc) throws PageNotFoundException;

	/**
	 * Does actions necessary after rendering (like committing the DB session)
	 */
	private void postRender() {
		if (sobjDB.isDBSessionActive()) {
			sobjDB.commitDBSession();
		}
	}

	private Template makeMsg(VelocityContext vc, String msgType, String message, String returnURL) {
		vc.put("msgtype", msgType);
		vc.put("msg", message);
		vc.put("returnURL", returnURL);
		return loadTemplate(TEMPLATE_MSG);
	}

	/**
	 * Return a template for an error message
	 */
	protected Template makeErrorMsg(VelocityContext vc, String message) {
		return makeErrorMsg(vc, message, "");
	}

	/**
	 * Return a template for an error message with a return URL
	 */
	protected Template makeErrorMsg(VelocityContext vc, String message, String returnURL) {
		return makeMsg(vc, "error", message, returnURL);
	}

	/**
	 * Return a template for a success message
	 */
	protected Template makeSuccessMsg(VelocityContext vc, String message) {
		return makeSuccessMsg(vc, message, "");
	}

	/**
	 * Return a template for a success message with a return URL
	 */
	protected Template makeSuccessMsg(VelocityContext vc, String message, String returnURL) {
		return makeMsg(vc, "success", message, returnURL);
	}

	protected Template loadTemplate(String path) {
		Template t = null;
		try {
			t = ve.getTemplate(TEMPLATE_ROOT + path );
		} catch (Exception e) {
			getLogger().warn("Failed to get template <" + path + ">");
		}
		return t;
	}

	protected DBService getDB() {
		return sobjDB;
	}

	protected Logger getLogger() {
		return sobjLogger;
	}

	protected ITranslation getTranslation() {
		return Translation.EN;
	}

	/**
	 * Creates a <code>Long</code> object from the content of the given
	 * <code>String</code> object, while handling internally any thrown
	 * exception.
	 * 
	 * @param value the <code>String</code> value
	 * 
	 * @return The <code>Long</code> value.
	 */
	protected static Long fromString (String value) {
		try {
			return (new Long(value));
		}
		catch (NumberFormatException ex){
			return null;
		}
	}
}
