package eu.sqooss.impl.service.messaging.senders.smtp;

import eu.sqooss.impl.service.messaging.senders.smtp.connection.SessionException;

import java.util.Vector;
import java.io.InputStream;

/**
 * SMTP Service Interface allows sending messages to a SMTP Server.
 */
public interface SMTP {

  /**
   * Sends a message, using the specified data.
   *
   * @param   receivers  the receivers of the message
   * @param   reply  the reply address
   * @param   message  the message to be sent
   *
   * @exception   SMTPException
   *               Thrown when an error occurs while executing SMTP
   *               service.
   * @exception   SessionException  if the current SMTP session is invalid
   */
  public void send(Vector receivers, String reply, String message)
    throws SMTPException, SessionException;

  /**
   * Sends a message, using the specified data.
   *
   * @param   receivers  the receivers of the message
   * @param   reply  the reply address
   * @param   message  the message to be sent
   *
   * @exception   SMTPException
   *               Thrown when an error occurs while executing SMTP
   *               service.
   * @exception   SessionException  if the current SMTP session is invalid
   */
  public void send(Vector receivers, String reply, InputStream message)
    throws SMTPException, SessionException;

  /**
   * Initializes user's session
   *
   * @exception SessionException on error
   */
  public void open() throws SessionException;


  /**
   * Disconnects current session connection(s)
   *
   * @exception SessionException if session cannot be closed. If this exception
   *      occurs application should try at least once again to close the
   *      session and then consider it closed.
   */
  public void close() throws SessionException;

}
