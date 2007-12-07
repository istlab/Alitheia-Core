package eu.sqooss.impl.service.messaging.senders.smtp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;

import org.osgi.framework.BundleContext;

import eu.sqooss.impl.service.messaging.MessagingServiceImpl;
import eu.sqooss.impl.service.messaging.senders.smtp.utils.LineReader;
import eu.sqooss.impl.service.messaging.senders.smtp.connection.Constants;
import eu.sqooss.impl.service.messaging.senders.smtp.connection.SASL;
import eu.sqooss.impl.service.messaging.senders.smtp.connection.SASLFactory;
import eu.sqooss.impl.service.messaging.senders.smtp.connection.SessionException;
import eu.sqooss.impl.service.messaging.timer.Timer;
import eu.sqooss.impl.service.messaging.timer.TimerListener;
import eu.sqooss.service.messaging.MessagingService;

/**
 * An SMTP session realizes and controls communication between a single client
 * and an SMTP server.
 */
public class SMTPSession implements SMTP, TimerListener {
    private Socket socket;
    private LineReader reader;
    private BufferedWriter writer;
    private boolean isOpen = false;
    private Properties properties;
    private int id;
    private static int currentId = -1;
    private SASLFactory saslFactory;
    private long timeout;
    private Timer timer;

    /**
     * Constucts an SMTPSession object.
     *
     * @param   bc  BundleContext object of the bundle, which uses this default connection.
     * @param   sessionProperties  set of properties that are used to initialize
     *          connection. These properties are used by the <code>open()</code> method
     *          and must include the following:
     *          Key                  Value
     *          Constants.HOST       ftp server host (IP address)
     *          Constants.PORT       ftp server port
     * @param   timeout  default timeout for each session created;
     */
    public SMTPSession(BundleContext bc, Properties sessionProperties, long timeout, Timer timer) {
    	MessagingServiceImpl.log("Creating SMTPSession", MessagingService.LOGGING_INFO_LEVEL);
        properties = sessionProperties;
        this.timeout = timeout;
        this.timer = timer;
        id = ++currentId;
    }

    public void open() throws SessionException {
        if (isOpen) return;
        MessagingServiceImpl.log("Opening SMTPSession to host: " + (String)properties.getProperty(Constants.HOST),
        		MessagingService.LOGGING_INFO_LEVEL);
        try {
            String server = properties.getProperty(Constants.HOST);
            String portStr = properties.getProperty(Constants.PORT);
            int port = (portStr != null)? Integer.parseInt(portStr) : 25;
            MessagingServiceImpl.log("Creating socket", MessagingService.LOGGING_INFO_LEVEL);
            socket = new Socket(server, port);
            reader = new LineReader(socket.getInputStream());
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            MessagingServiceImpl.log("Socket opened", MessagingService.LOGGING_INFO_LEVEL);
            timer.addNotifyListener(this, timeout);
            String answer = reader.readLine();
            MessagingServiceImpl.log("Receiving answer from SMTP: " + answer, MessagingService.LOGGING_INFO_LEVEL);

            int resValue = getCode(answer);
            if (resValue != 220)  {
            	MessagingServiceImpl.log("Error in open method. Answer expected: 220. Answer received: " + answer,
                		MessagingService.LOGGING_WARNING_LEVEL);
                throwException("Error in open method. Answer expected: 220. Answer received: " + answer, null, resValue, answer);
            }
            sayEHello(server);
            MessagingServiceImpl.log("Session opened. Session ID is: " + id, MessagingService.LOGGING_INFO_LEVEL);
        } catch (Exception me) {
        	MessagingServiceImpl.log("Unable to establish connection with server: " + me.getMessage(),
            		MessagingService.LOGGING_WARNING_LEVEL);
            throw new SessionException("Unable to establish connection with server." +
                    "\nThe exception is : " + me.getMessage(), me);
        }
        isOpen = true;
    }

    private int getCode(String answer) throws SMTPException {
        try {
            Integer i = new Integer(answer.substring(0, 3));
            return i.intValue();
        } catch (Exception e) {
            String err = getSessionIdString() + "Error while trying to read SMTP server's answer: " + answer;
            MessagingServiceImpl.log(err, MessagingService.LOGGING_WARNING_LEVEL);
            throw new SMTPException(err);
        }
    }

    private void throwException(String message, Exception e, int code, String answer) throws SMTPException {
        String s = getSessionIdString() + message;
        MessagingServiceImpl.log(s + "\nThe exception is :" + e.getMessage(), MessagingService.LOGGING_WARNING_LEVEL);
        if (code != 0) {
            throw new SMTPException(s);
        } else {
            throw new SMTPException(s, code, answer);
        }
    }

    private static final char[] specialChars = {'<', '>', '(', ')', '[', ']', '\\', '.',
        ',', ';', ':', '@', '"'};
    private static final String special = new String(specialChars);

    private String correctReversePath(String reversePath) {
        int atIndex = reversePath.lastIndexOf('@');
        if (atIndex == -1) return reversePath;
        String localPart = reversePath.substring(0, atIndex);
        if (localPart.startsWith("\"") && localPart.endsWith("\"")) return reversePath;
        boolean needsQuoting = false;
        StringBuffer corrected = new StringBuffer();
        for(int i = 0; i < localPart.length(); i++) {
            char ch = localPart.charAt(i);
            if ((special.indexOf(ch) != -1) || (ch < 32) || (ch == 127)) {
                if ((i > 0) && (localPart.charAt(i - 1) != '\\')) corrected.append('\\');
            }
            if (ch == ' ') needsQuoting = true;
            corrected.append(ch);
        }
        if (needsQuoting) {
            corrected.insert(0, '"');
            corrected.insert(corrected.length(), '"');
        }
        return corrected.toString() + reversePath.substring(atIndex);
    }

    /**
     * Sends a message, using the specified data.
     *
     * @param   receivers  the receivers of the message
     * @param   reply  the reply address
     * @param   message  the message to be sent
     *
     * @exception   SMTPException
     *               Thrown when an other error occurs while executing SMTP
     *               service.
     * @exception   SessionException if the method is called on an invalid session.
     */
    public synchronized void send(Vector<String> receivers, String reply, String message)
    throws SMTPException, SessionException {
        send(receivers, reply, new ByteArrayInputStream(message.getBytes()));
    }

    /**
     * Sends a message, using the specified data.
     *
     * @param   receivers  the receivers of the message
     * @param   reply  the reply address
     * @param   message  the message to be sent
     *
     * @exception   SMTPException
     *               Thrown when an other error occurs while executing SMTP
     *               service.
     * @exception   SessionException if the method is called on an invalid session.
     */
    public synchronized void send(Vector<String> receivers, String reply, InputStream message)
    throws SMTPException, SessionException {
    	MessagingServiceImpl.log("Executing SMTP send message.", MessagingService.LOGGING_INFO_LEVEL);
        try  {
            BufferedReader messageReader =  new BufferedReader(new java.io.InputStreamReader(message));
            String answer;
            String line;
            int value;

            MessagingServiceImpl.log("Sending command to SMTP: MAIL FROM:<" + correctReversePath(reply) + ">",
            		MessagingService.LOGGING_INFO_LEVEL);
            writer.write("MAIL FROM:<" + correctReversePath(reply) + ">\r\n");
            writer.flush();
            answer = reader.readLine();
            MessagingServiceImpl.log("Receiving answer from SMTP: " + answer,
            		MessagingService.LOGGING_INFO_LEVEL);
            value = getCode(answer);
            if (value != 250)  {
                throwException("Expected answer: 250 Requested mail action okay, completed. Answer received: " + answer, null, value, answer);
            }
            boolean isRcptOk = true;
            String recipient = null;
            for (int i = 0; i < receivers.size(); i++) {
                recipient = (String) receivers.elementAt(i);
                MessagingServiceImpl.log("Sending command to SMTP: RCPT TO:<" + recipient + ">",
                		MessagingService.LOGGING_INFO_LEVEL);
                writer.write("RCPT TO:<" + recipient + ">\r\n");
                writer.flush();
                answer = reader.readLine();
                MessagingServiceImpl.log("Receiving answer from SMTP: " + answer, MessagingService.LOGGING_INFO_LEVEL);
                if (getCode(answer) != 250) {
                    isRcptOk = false;
                    break;
                }
            }
            if (!isRcptOk) {
                rset();
                throwException("Recipient <" + recipient + "> rejected by SMTP server.", null, getCode(answer), answer);
            }
            MessagingServiceImpl.log("Sending command to SMTP: DATA", MessagingService.LOGGING_INFO_LEVEL);
            writer.write("DATA\r\n");
            writer.flush();
            answer = reader.readLine();
            MessagingServiceImpl.log("Receiving answer from SMTP: " + answer,
            		MessagingService.LOGGING_INFO_LEVEL);
            value = getCode(answer);
            if (value != 354)  {
                throwException("Expected answer: 354 Start mail input. Answer received: " + answer, null, value, answer);
            }
            while( (line = messageReader.readLine())!= null ){
                if (line.startsWith(".")) {
                    line = "." + line;
                }
                MessagingServiceImpl.log("Sending command to SMTP: " + line, MessagingService.LOGGING_INFO_LEVEL);
                writer.write(line + "\r\n");
            }
            MessagingServiceImpl.log("Sending command to SMTP: .\\r\\n", MessagingService.LOGGING_INFO_LEVEL);
            writer.write("\r\n.\r\n");
            writer.flush();

            answer = reader.readLine();
            MessagingServiceImpl.log("Receiving answer from SMTP: " + answer, MessagingService.LOGGING_INFO_LEVEL);
            value = getCode(answer);
            if (value != 250)  {
                throwException("Expected answer: 250 Requested mail action okay, completed. Answer received: " + answer, null, value, answer);
            }
            MessagingServiceImpl.log("The message is successfully sent", MessagingService.LOGGING_INFO_LEVEL);
        } catch(IOException e) {
            throwException("IOException in send method.", e, 0, null);
        }
    }

    private void rset() throws IOException {
    	MessagingServiceImpl.log("Sending command to SMTP: RSET", MessagingService.LOGGING_INFO_LEVEL);
        writer.write("RSET \r\n");
        writer.flush();
        String answer = reader.readLine();
        MessagingServiceImpl.log("Receiving answer from SMTP: " + answer, MessagingService.LOGGING_INFO_LEVEL);
    }



    private void sayHello(String senderHost) throws SMTPException, IOException, SessionException {
        String answer = null;
        int value = 0;
        MessagingServiceImpl.log("Sending command to SMTP: HELO", MessagingService.LOGGING_INFO_LEVEL);
        writer.write("HELO " + senderHost + "\r\n");
        writer.flush();

        answer = reader.readLine();
        MessagingServiceImpl.log("Receiving answer from SMTP: " + answer, MessagingService.LOGGING_INFO_LEVEL);
        value = getCode(answer);
        if (value != 250)  {
            throwException("Error in open method. Expected answer: 250 Requested mail action okay, completed. Answer received: " + answer, null, value, answer);
        }
    }

    private void sayEHello(String senderHost) throws SMTPException, IOException, SessionException {
        String answer = null;
        String toAuthorize=null;
        int value = 0;
        MessagingServiceImpl.log("Sending command to SMTP: EHLO", MessagingService.LOGGING_INFO_LEVEL);
        writer.write("EHLO " + senderHost + "\r\n");
        writer.flush();

        answer = reader.readLine();
        MessagingServiceImpl.log("Receiving answer from SMTP: " + answer, MessagingService.LOGGING_INFO_LEVEL);
        value = getCode(answer);
        if (value != 250)  {
            if(value==500){
                sayHello(senderHost);
                return;
            }
            throwException("Error in open method. Expected answer: 250 Requested mail action okay, completed. Answer received: " + answer, null,value,answer);
        }
        while(answer.charAt(3)=='-'){
            if(answer.length()>=9&&answer.substring(4,9).equalsIgnoreCase("AUTH ")){
                toAuthorize=answer;
            }
            answer=reader.readLine();
            MessagingServiceImpl.log("Receiving answer from SMTP: " + answer, MessagingService.LOGGING_INFO_LEVEL);
        }
        Object user = properties.get(Constants.USER);
        if(toAuthorize!=null && user != null && !user.equals("")) {
            authorize(toAuthorize.substring(4));
        }
    }

    private void sayBye() throws IOException, SMTPException {
        String answer = null;
        int value = 0;
        MessagingServiceImpl.log("Sending command to SMTP: QUIT", MessagingService.LOGGING_INFO_LEVEL);
        writer.write("QUIT\r\n");
        writer.flush();
        answer = reader.readLine();
        MessagingServiceImpl.log("Receiving answer from SMTP: " + answer,
        		MessagingService.LOGGING_INFO_LEVEL);
        value = getCode(answer);
        if (value != 221)  {
            throwException("Error in close method. Expected answer: 221 <domain> Service closing transmission channel. Answer received: " + answer, null, value, answer);
        }
    }

    public void close() {
    	MessagingServiceImpl.log("Disconnects current session connection(s).", MessagingService.LOGGING_INFO_LEVEL);
        if (isOpen) {
            try {
                sayBye();
                timer.removeNotifyListener(this);
                socket.close();
                writer = null;
                reader = null;
                isOpen = false;
                MessagingServiceImpl.log("Closed the session with id: " + id, MessagingService.LOGGING_INFO_LEVEL);
            } catch (Exception e) {
            	MessagingServiceImpl.log(getSessionIdString() + " an error has occurred while trying to close: " + e.getMessage(),
                		MessagingService.LOGGING_WARNING_LEVEL);
            }
        }
    }

    public void timer() {
        timedOut();
    }

    /**
     * Called by notifier (SMTPSessionTimeoutThread) when current session is timed out and
     * called by SMTPSender when the service is stopped.
     */
    public synchronized void timedOut() {
    	MessagingServiceImpl.log("Timeout for session with id: " + id, MessagingService.LOGGING_INFO_LEVEL);
        try {
            if (socket != null) {
                socket.close();
            }
            writer = null;
            reader = null;
            isOpen = false;
            MessagingServiceImpl.log("Closed the session with id: " + id, MessagingService.LOGGING_INFO_LEVEL);
        } catch (Exception e) {
        	MessagingServiceImpl.log(getSessionIdString() + " an error has occurred while trying to close: " + e.getMessage(),
            		MessagingService.LOGGING_WARNING_LEVEL);
        }
    }

    protected String getSessionIdString() {
        return "[SMTPSession_" + id + "] ";
    }

    public boolean authorize(String request) throws SMTPException {
    	MessagingServiceImpl.log("AUTH Started. Methods on server: " + request,
        		MessagingService.LOGGING_INFO_LEVEL);
        if(saslFactory==null){
            return false;
        }
        Vector < String > securityMethods = new Vector < String >();
        StringTokenizer st = new StringTokenizer(request," ");
        String command = "";
        command=st.nextToken();
        if(!command.equalsIgnoreCase("AUTH")){
            return false;//Incorrect command
        }
        while(st.hasMoreTokens()){
            String tok = st.nextToken();
            if(saslFactory.getSASL(tok.toUpperCase())!= null){
                securityMethods.addElement(tok.toUpperCase());
            }
        }

        int index=0;
        try{
            int code454=0;

            while(index<securityMethods.size()){
                String method = (String)securityMethods.elementAt(index);
                MessagingServiceImpl.log("AUTH with method: " + method, MessagingService.LOGGING_INFO_LEVEL);
                SASL current = saslFactory.getSASL(method);
                String frst = current.getResponse(properties, "");
                MessagingServiceImpl.log("Sending command to SMTP: AUTH " + securityMethods.elementAt(index) + " "+((frst != null)?frst:""),
                		MessagingService.LOGGING_INFO_LEVEL);
                if(frst==null){
                    writer.write("AUTH "+securityMethods.elementAt(index)+"\r\n");
                } else {
                    writer.write("AUTH "+securityMethods.elementAt(index)+" "+frst);
                }
                writer.flush();
                String response = reader.readLine();
                MessagingServiceImpl.log("Receiving answer from SMTP: " + response, MessagingService.LOGGING_INFO_LEVEL);
                StringTokenizer tok=new StringTokenizer(response,"- ");
                int code = Integer.parseInt(tok.nextToken());
                if(code/100==2){
                    //positive - authentication ok;
                    return true;
                }
                if(code==334){
                    String server=tok.nextToken(" ");
                    frst=current.getResponse(properties, server);
                    if(frst==null){
                        frst="\n\r";//empty string not null send to server.
                    }
                    MessagingServiceImpl.log("Sending command to SMTP: " + frst, MessagingService.LOGGING_INFO_LEVEL);
                    writer.write(frst);
//                  System.out.println("Second Step : "+frst+":"+properties.getProperty(Constants.USER)+":"+properties.getProperty(Constants.PASS));
                    writer.flush();
                    response=reader.readLine();
                    MessagingServiceImpl.log("Receiving answer from SMTP: " + response,
                    		MessagingService.LOGGING_INFO_LEVEL);
//                  System.out.println("Second response : "+response);
                    tok=new StringTokenizer(response,"- ");
                    code = Integer.parseInt(tok.nextToken());
                    if(code/100==2){
                    	MessagingServiceImpl.log("AUTH OK.", MessagingService.LOGGING_INFO_LEVEL);
                        //positive - authentication ok
                        return true;
                    }else{
                        // --- FOR LOGIN SASL --- //
                        if (code == 334) {
                            server = tok.nextToken(" ");
                            frst = current.getResponse(properties, server);
                            if (frst == null) {
                                frst = "\n\r"; //empty string not null send to server.
                            }
                            MessagingServiceImpl.log("Sending command to SMTP: " + frst, MessagingService.LOGGING_INFO_LEVEL);
                            writer.write(frst);
                            writer.flush();
                            response = reader.readLine();
                            MessagingServiceImpl.log("Receiving answer from SMTP: " + response,
                            		MessagingService.LOGGING_INFO_LEVEL);
                            tok = new StringTokenizer(response, "- ");
                            code = Integer.parseInt(tok.nextToken());
                            if (code/100 == 2) {
                            	MessagingServiceImpl.log("AUTH OK.", MessagingService.LOGGING_INFO_LEVEL);
                                // positive - authentication ok
                                return true;
                            } else {
                                // negative see what is the error.
                                if(code==454){
                                    if(code454==3){
                                        index++;
                                        continue;
                                    }
                                    MessagingServiceImpl.log("SMTP error 454 - Temporary authentication failure. Waiting 100ms.",
                                    		MessagingService.LOGGING_INFO_LEVEL);
                                    code454++;
                                    try {// tova da ne se zaciklja bezkrajno - samo 3 pyti.
                                        Thread.sleep(100);
                                    } catch (InterruptedException e) {
                                    }
                                    continue;//temporary server failure, try again same mechanism until another error occurs or successful.
                                }else{
                                	MessagingServiceImpl.log("AUTH Failed for current method. Error code is: " + code,
                                    		MessagingService.LOGGING_INFO_LEVEL);
                                    index++;
                                    code454=0;
                                    continue;//all other errors mean try next mechanism if any.
                                }
                            }
                        }
                        // --- END LOGIN SASL --- //
                        // negative see what is the error.
                        if(code==454){
                            if(code454==3){
                                index++;
                                continue;
                            }
                            MessagingServiceImpl.log("SMTP error 454 - Temporary authentication failure. Waiting 100ms.",
                            		MessagingService.LOGGING_INFO_LEVEL);
                            code454++;
                            try {// tova da ne se zaciklja bezkrajno - samo 3 pyti.
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                            }
                            continue;//temporary server failure, try again same mechanism until another error occurs or successful.
                        }else{
                        	MessagingServiceImpl.log("AUTH Failed for current method. Error code is: " + code,
                            		MessagingService.LOGGING_INFO_LEVEL);
                            index++;
                            code454=0;
                            continue;//all other errors mean try next mechanism if any.
                        }
                    }
                }
                index++;
            }
        }catch(IOException ioe){
        	MessagingServiceImpl.log("Error while authorizing: " + ioe.getMessage(), MessagingService.LOGGING_WARNING_LEVEL);
        }
        MessagingServiceImpl.log("AUTH Failed!", MessagingService.LOGGING_WARNING_LEVEL);
        return false;
    }

    public void setSASLFactory(SASLFactory saslFactory) {
        this.saslFactory = saslFactory;
    }

}