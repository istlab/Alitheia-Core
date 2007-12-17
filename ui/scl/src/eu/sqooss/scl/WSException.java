package eu.sqooss.scl;

public class WSException extends Exception {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * @see java.lang.Exception#Exception()
     */
    public WSException() {
        super();
    }
    
    /**
     * @see java.lang.Exception#Exception(java.lang.String message)
     */
    public WSException(String message) {
        super(message);
    }
    
    /**
     * @see java.lang.Exception#Exception(java.lang.Throwable cause)
     */
    public WSException(Throwable cause) {
        super(cause);
    }
    
    /**
     * @see java.lang.Exception#Exception(java.lang.String message, java.lang.Throwable cause)
     */
    public WSException(String message, Throwable cause) {
        super(message, cause);
    }

}
