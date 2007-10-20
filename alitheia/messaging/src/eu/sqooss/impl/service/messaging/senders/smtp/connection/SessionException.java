package eu.sqooss.impl.service.messaging.senders.smtp.connection;

/**
 * This type of exception could occur while performing methods of the services
 * which implement the Session interface.
 */
public class SessionException extends Exception {
  private static final long serialVersionUID = 1L;

  private Exception ex;


  /**
   * Creates a new exception with a specified message.
   *
   * @param message the exception's message
   */
  public SessionException( String message ) {
    super( message );
  }


  /**
   * Creates a SessionException with a specified message and an exception,
   * which had caused an error while connecting/disconnecting.
   *
   * @param message descripion of the exception
   * @param ex exception, caused the error.
   */
  public SessionException( String message, Exception ex ) {
    super( message );
    this.ex = ex;
  }


  /**
   * Gets the exception, associated with this SessionException.
   *
   * @return the exception, caused an error, or null if there is no associated
   *      exception
   */
  public Exception getException() {
    return ex;
  }
}
