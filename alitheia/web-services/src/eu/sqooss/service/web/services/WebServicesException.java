package eu.sqooss.service.web.services;

/**
 * The web services service throws <code>WebServicesException</code>.
 * Axis2 server returns the exception to the client in the SOAP fault element. 
 */
public class WebServicesException extends Exception {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * @see java.lang.Exception#Exception()
     */
    public WebServicesException() {
        super();
    }
    
    /**
     * @see java.lang.Exception#Exception(java.lang.String message)
     */
    public WebServicesException(String message) {
        super(message);
    }
    
    /**
     * @see java.lang.Exception#Exception(java.lang.Throwable cause)
     */
    public WebServicesException(Throwable cause) {
        super(cause);
    }
    
    /**
     * @see java.lang.Exception#Exception(java.lang.String message, java.lang.Throwable cause)
     */
    public WebServicesException(String message, Throwable cause) {
        super(message, cause);
    }
}
