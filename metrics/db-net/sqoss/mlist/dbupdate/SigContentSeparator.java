package paxosk.sqoss.mlist.dbupdate;

import edu.cmu.minorthird.text.CharAnnotation;
import edu.cmu.minorthird.util.LineProcessingUtil;
import jangada.*;
import javax.mail.internet.MimeMessage;
import javax.mail.*;
import javax.mail.internet.*;
import org.apache.log4j.*;
import org.apache.commons.configuration.*;
import paxosk.classes.common.*;
import paxosk.string.common.*;
import java.util.*;
import java.io.*;


/**
 * E-mail feature extractor class. Contains wraper around the Jangada project and other methods. See http://www.
 * cs.cmu.edu/~vitor/codeAndData.html
 * Jangada use the "MinorThird" java package, a set of classes for  storing text, annotating text, and learning
 * to extract entities and categorize text.
 * Jangada wraper methods are marked. This class is used by {@link paxosk.sqoss.mlist.MailInserter MailInserter}
 *
 * Roles:
 * -extract signature
 * -extract contents not including the signature
 * -extract contents including the signature either by using Javamail -method (1)- or by using Jangada -method (2),
 * currently using JAVAMAIL
 *
 */
public class SigContentSeparator 
{ 
    //the predictorn used by Jangada methods
    private SigFilePredictor sigpred = new SigFilePredictor();		     
    //system properties
    private Properties sysProps_=System.getProperties();
    //mail session
    private Session ses_=Session.getInstance(sysProps_,null);        
    //nmy properties
    private PropertiesConfiguration myProps_=null;
    //the logger; common with the main processing and inserting mail class
    private Logger logger_=null;
    //read from the properties; if true the extractAllContentsJavamail method is used for
    //content retrieval, if false extractAllContentsJangada is used instead.
    private boolean useJavamail_;
    //the number of lines the main content of a mail message is supposed to have
    private int iContentLines_;    
    
    
    /**
     * Constructor:
     * (1) reads the properties file and (2) attaches an already existing logger 
     * to this class
     * @param logger the already existing logger to be attached
     */
    public SigContentSeparator(Logger logger,PropertiesConfiguration props)
    {
        //read props file
        myProps_=props;
        
        //read property on whether or not retrieving content by Jangada
        useJavamail_=Boolean.getBoolean((String)myProps_.getProperty("MSG_MAIN_CONTENT_JAVAMAIL"));
        
        //read property on the number of lines the main content of a mail message is supposed to have
        iContentLines_=Integer.valueOf((String)myProps_.getProperty("MSG_CONTENT_LINES"));
        
        //attach logger
        logger_=logger;
    }//Extractor    
    
    
    /**
     * Extract the signature from the MIME message; Jangada wraper
     * @param msgfull string representation of the MIME message
     * @return the signature lines
     *
     * BE CAREFUL! make sure msgfull doesn't enclose an attachment.
     * Sometimes unknown source ArrayIndexOutOfBoundsException exceptions
     * are thrown
     */
    public String extractSignature(String msgfull)
    {
        String s="";
        
        try {
             s=sigpred.getSignatureLines(msgfull);  
        }
        catch (ArrayIndexOutOfBoundsException e) {
            logger_.error("Jangada bug! Failed o extract signature "+ MyClassUtils.getStackTrace(e));
        }
        finally {
            return s;
        }
        
        //return  s;
    }//extractSignature
 
    
    /**
     * Extract the contents not including the signature from the MIME 
     * message; Jangada wraper
     * @param msgfull string representation of the MIME message
     * @return the message contents lines
     */
    public String extractContentWithoutSignature(String msgfull)
    {   
        return sigpred.getMsgWithoutSignatureLines(msgfull);
    }//extractContentWithoutSignature
    

    /**
     * Wrapper method around either extractAllContentsJavamail or
     * extractAllContentsJaganda for extracting the main context of a message
     * @param msgfull the string representation of the message
     */
    public  String extractAllContents(String msgfull)
    {       
        if (useJavamail_)
        {
            return extractAllContentsJavamail(msgfull);
        }
        else
        {
            return extractAllContentsJangada(msgfull);
        }
                
    }//extractAllContents

    
    
    /**
     * Returns the mail message, but with all attachmets removed;
     * useful since attachments are usually too big to fit in an SQL field.
     * @param p Part the message to be processed
     * @param s the String that will be returned is passed as a parameter
     * @return String the message contents without the attachment
     *
     * @throws MessagingException
     * @throws IOException
     */
    public String extractAllContensNoAttachments(Part p, String s) throws MessagingException, IOException
    {
	if (p.isMimeType("text/plain")) {
            s+=(String)p.getContent();
	} 
        else if (p.isMimeType("multipart/*")) {
	    Multipart mp = (Multipart)p.getContent();
	    int count = mp.getCount();
	    for (int i = 0; i < count; i++)
		extractAllContensNoAttachments(mp.getBodyPart(i),s);
	} 
        else //whatever else the type can be; for example an input stream corresonding to an attachment
        {
            
//            Object o=p.getContent();
//            if (o instanceof InputStream) 
//            {
//                System.err.println("This is just an input stream/n---------------------------");
//                InputStream is = (InputStream)o;
//                int c;
//                while ((c = is.read()) != -1)
//                    System.out.write(c);
//            } 
	}//else        
        
        return s;
    }


    
    /////////////////////////////////////HELPER//METHODS///////////////////////////////////////////////
    ////////////////////////////////JAVAMAIL///HELPERS///////////////////////////////////////
    /**
     * Extract all the contents from the MIME message, including 
     * the signature; based on Javamail
     * @param msgfull string representation of the MIME message
     * @return the content lines
     */
    private  String extractAllContentsJavamail(String msgfull)
    {        
        MimeMessage mm=null;
        try {
            mm = new MimeMessage(ses_, new ByteArrayInputStream(msgfull.getBytes()));
        } catch (MessagingException ex) {
            if (logger_!=null) {
                logger_.error("failed to create MimeMessage instance!\n"+MyClassUtils.getStackTrace(ex)+"\ncontinuing...");            
            } else {
                System.err.println("failed to create MimeMessage instance!\n"+MyClassUtils.getStackTrace(ex)+"\ncontinuing...");
            }                
        }   
        
        Part p=(Part)mm; //wrap MimeMessage to Part
        int iRecur=0;   //keep track of the number of recursions, ie the depth the tree representation 
                        //of the message, ie the number of embedded multipart-* MIME types
	
        String  sMainContent="";
        try
        {
             getMainContent(p,iRecur);
        }
        catch(MessagingException e)
        {
            if (logger_!=null) {
                logger_.error("failed to get message main content...\n"+MyClassUtils.getStackTraceTabbed(e));
            } else {
                System.err.println("failed to get message main content...\n"+MyClassUtils.getStackTraceTabbed(e));
            }
        }
        catch(IOException e)
        {
            if (logger_!=null) {
                logger_.error("failed to get message main content...\n"+MyClassUtils.getStackTraceTabbed(e));
            } else {
                System.err.println("failed to get message main content...\n"+MyClassUtils.getStackTraceTabbed(e));
            }
        }
        
        return sMainContent;
    }//extractAllContentsJavamail
    
    
    /**
     * Given a MimeMessage or a part of a message recursively searches the message
     * tree structure to find the main content of the message. Called by 
     * {@link #extractAllContentsJavamail(String)}
     * @param p the message or the part (tree node) of the message
     * @return the part of the message as string, which is the main content or
     * empty string, indicating that something has gone wrong
     */
    private String getMainContent(Part p,int iRecur) throws MessagingException,IOException
    {
	if (p.isMimeType("text/plain")) 
        {
            String s=(String)p.getContent();
            if (checkAndReturn(s)) {
                if (logger_!=null) {
                    logger_.debug("text-plain approved as message main content");
                } else {
                    System.err.println("text-plain approved as message main content");
                }
                
                return s;
            }
            
            if (logger_!=null) {
                logger_.debug("text-plain not approved as message main content");
            } else {
                System.err.println("text-plain not approved as message main content");
            }
	} 
        else if (p.isMimeType("multipart/*")) 
        {
	    Multipart mp = (Multipart)p.getContent();
	    int count = mp.getCount();
            iRecur++;            
	    for (int i = 0; i < count; i++) 
            {
                if (logger_!=null) {                    
                    logger_.debug("doing now recursion number "+iRecur+" for body part number "+i);
                } else {
                    System.err.println("doing now recursion number "+iRecur+" for body part number "+i);
                }
                
		getMainContent(mp.getBodyPart(i),iRecur);
            }
	}
        else 
        {	
            if (logger_!=null) {
                logger_.warn("not a multipart-* of plain-text MIME detected");
            } else {
                System.err.println("not a multipart-* of plain-text MIME detected");
            }
	}
        
        //indicates, that sth has gone wrong; log it
        if (logger_!=null) {            
            logger_.error("Empty string returned as message main content... not exiting, but investigate");
        } else {
            System.err.println("Empty string returned as message main content... not exiting, but investigate");
        }
        
        return "";
    }//getMainContent
    
    
    /**
     * Check if this is message content or not, by using simple heuristics. Called by
     * {@link #extractAllContentsJavamail(String)}.
     * @param s the text believed to be the message content
     * @return true if the text is part of the main message content
     * and false otherwise
     */
    private boolean checkAndReturn(String s)
    {
        
        //int iLines=s.split(sysProps_.getProperty("line.separator")).length;
        int iLines=StringUtils.countLines(s);
        boolean isContent=false;
        
        //heuristic 1; if number of line are above a certain threshold
        if (iLines>iContentLines_)
        {
            isContent=true;
        }
        
        return isContent;
    }//checkAndReturn
    ////////////////////////////////JAVAMAIL///HELPERS///////////////////////////////////////
    
    
    ////////////////////////////////JAGANDA///HELPERS///////////////////////////////////////
    /**
     * Extract all the contents from the MIME message, including 
     * the signature; Jangada wraper
     * @param msgfull string representation of the MIME message
     * @return the main content lines
     */
    private  String extractAllContentsJangada(String msgfull)
    {
        String sig=sigpred.getSignatureLines(msgfull);
        String msg=sigpred.getMsgWithoutSignatureLines(msgfull);;
        String all="";
        
        if (msg!=null) {
            all+=msg;
        }
        
        if (sig!=null) {
            all+=sig;
        }
        
        return all;        
    }//extractAllContentsJangada
    ////////////////////////////////JAGANDA///HELPERS///////////////////////////////////////    
    /////////////////////////////////////HELPER//METHODS///////////////////////////////////////////////    
    
    
}
