package eu.sqooss.impl.service.webadmin.servlets.exceptions;

/**
 * This exception is thrown when a servlet is called with an URL it cannot handle
 */
@SuppressWarnings("serial")
public class PageNotFoundException extends Exception {

	public PageNotFoundException() {
		super();
	}

	public PageNotFoundException(String arg0, Throwable arg1, boolean arg2,
			boolean arg3) {
		super(arg0, arg1, arg2, arg3);
	}

	public PageNotFoundException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public PageNotFoundException(String arg0) {
		super(arg0);
	}

	public PageNotFoundException(Throwable arg0) {
		super(arg0);
	}

}
