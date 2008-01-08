package paxosk.sqoss.mlist.dbupdate;

import java.nio.*;
import java.nio.channels.*;
import java.nio.charset.*;
import javax.mail.internet.MimeMessage;
import javax.mail.*;
import java.util.*;
import java.io.*;
import org.apache.log4j.*;
import org.apache.commons.configuration.*;
import paxosk.log.loggers.*;
import paxosk.classes.common.*;


/**
 * Iterates through a list of mailing-list MIME messages; returns the
 * next message (MimeMessage, String representation) to be processed.
 */
public class MailIterator 
{	
    //the path to the mail dir, read from the props
    private File mailPathFile_=null;
    //the messages will be buffered with this size
    private int mailBufferSize_=0;		
    //file, where the full paths to the message files are kept
    private File msgPathFile_=null;	
    //an iterator over the collection of messages
    private Iterator<MimeMessage> msgIter_=null;
    //output stream to the tmp file	
    private BufferedWriter out_=null;
    //input stream to the tmp file	
    private BufferedReader in_=null;
    //last path to message to be read
    private String lastPath_="";
    //the string representation of the msg; including mime header,values,etc
    private String msgtxt_="";
    //table storing header fields and their corresponding values
    private  Hashtable<String,String> hHeaders_=new Hashtable<String,String>();
    //system properties
    private Properties sysProps_=System.getProperties();
    //the logger
    private Logger logger_=null;
    

    /**
     * Constructor: 
     * @param path the path to the property files dir
     */
    public MailIterator(PropertiesConfiguration props,Logger logger)
    {
        mailPathFile_=new File((String)props.getProperty("MAIL_STORE_PATH"));
        mailBufferSize_=Integer.valueOf((String)props.getProperty("MAIL_BUFFER_SIZE"));
        msgPathFile_=new File((String)props.getProperty("TMP_DIR")+"msgpaths.dat");
        
        //delete the message listing file if it already exists
        if (msgPathFile_.delete())
        {
            System.err.println("Old message listing file has been deleted ...");
        }
        
        //create a new message listing file
        try {                    
            msgPathFile_.createNewFile();        
        } 
        catch (IOException ex) {
            ex.printStackTrace();
        }        
        
        //create an input stream to the message listing file
        try {
            in_=new BufferedReader(new FileReader(msgPathFile_)); 
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
            System.exit(-1);
        } 
        
        //initialize the logger
        logger_=logger;
        storeMsgFilenames(); //create the absolute message path list
    }//MailIterator


    /**
     * Creates a file, which keeps the listing of all the absolute paths to the message files.
     * Calls the -wraper around- {@link #createMessageList(File) createMessageList(File)} method to to the actual
     * reading of the message files and for writing the paths to the list.
     * msgPathFile_: the absolute path to the directory, which stores the mailing-list messages
     */
    public void storeMsgFilenames()
    {
        try
        {
                out_= new BufferedWriter(new FileWriter(msgPathFile_));
                createMessageList(mailPathFile_);
                out_.flush();
                out_.close();
        }
        catch(IOException e)
        {
                logger_.fatal("Failed to open or write file storing message filenames... exiting"+ 
                        msgPathFile_.getAbsolutePath()+'\n'+MyClassUtils.getStackTrace(e));
                System.exit(-1);
        }		
    }//storeMsgFilenames


    /**
     * Is there another message  to process? Be careful! multiple calls to this method
     * without calls to the {@ link #getNextMessage() getNextMessage()} will bypass the current 
     * message and go to the next one.
     * @return true if it exists and false otherwise
     */
    public boolean hasNextMessage()
    {
        //essentially reads the next line from the tmp file, which lists the absolute
        //paths to  the messages. If there is not another line, then we have reached the end 
        try
        {
            lastPath_=in_.readLine();            
        }
        catch(IOException e)
        {
            logger_.fatal("Failed to read filename from tmp file: "+msgPathFile_.getAbsolutePath()+
                            +'\n'+MyClassUtils.getStackTrace(e));
            System.exit(-1);
        }

        if (lastPath_==null) {
                return false;
        }
        else {
                return true;
        }
    }//hasNextMessage

    
    /**
     * Gets the absolute path of the file/message, that is currently being processed.
     * @return String the current absolute path 
     */
    public String getCurrentPath()
    {
        return lastPath_;
    }//getCurrentPath
    
    
    /**
     * Get the next MimeMessage object of the directory
     * @return the MimeMessage
     */
    public MimeMessage getNextMessage()
    {
        return getMIMEMessage(new File(lastPath_));
    }//getNextMessage	        


    /**
     * Get the string representation of the current MIME message
     * @return the string representation
     */
    public String getCurrentMessageString()
    {
        return msgtxt_;
    }//getCurrentMessageString
    
    
    //////////////////////////////////////////////HELPERS/////////////////////////////////////////////////    
    /**
     * Recursively read the filenames of all messages contained within the directory and write 
     * each absolute path of the contained files (MIME messages) to the given file list.
     * @param f the name of the directory, where the mailing-list MIME messages
     * are stored
     */
    private void createMessageList(File f) throws IOException
    {	
        if (f.isDirectory()) 
        {             
            String[] nodes = f.list();

            for (int i=0; i<nodes.length; i++) 
            {
                createMessageList(new File(f, nodes[i]));
            }
        } 
        else 
        {
                out_.write(f.getAbsolutePath());
                out_.newLine();
        }
    }//readMessageList
	
	

    /**
     * Given a file, it returns the MimeMessage representation
     * of it.
     * @param f the File to the MIME message
     * @return the MimeMessage representation
     */
    private MimeMessage getMIMEMessage(File f)
    {
        try
        {
                msgtxt_=""; //reset the string representation back to ""
                //read the MIME message in memory, as a string
       
/*              A BufferedReader reading -the one below- will fail! The reason is 
 *              because attachments may contain a lot of data without any newline
 *              characters - stop characters for readlines! The buffer will be 
 *              expanded and expanded and so on, until it reaches a newline character.
 *              This could take LONG
 */             
                FileChannel fc=new RandomAccessFile(f,"r").getChannel();
                ByteBuffer bb=fc.map(FileChannel.MapMode.READ_ONLY,0,(int)fc.size());
                Charset chset=Charset.forName("ISO-8859-1");
                CharsetDecoder dec=chset.newDecoder();
                msgtxt_=new String(dec.decode(bb).toString());
                fc.close();
                
//                BufferedReader b=new BufferedReader(new FileReader(f));
//                String line="";
//
//                do
//                {
//                        if ((line=b.readLine())!=null)
//                        {
//                                msgtxt_+=line+'\n';
//                        }
//
//                } while (line!=null);

                //create a session and get the MimeMessage
                Session session=Session.getInstance(sysProps_,null);
                MimeMessage msg = new MimeMessage( session, new ByteArrayInputStream(msgtxt_.getBytes() ) );

                return msg;			
        }
        catch(FileNotFoundException e)
        {
            logger_.error("Message file not found: "+ f.getName()+"\n"+MyClassUtils.getStackTrace(e));
        }
        catch(MessagingException e)
        {
            logger_.error("Failed to process message: "+ f.getName()+"\n"+MyClassUtils.getStackTrace(e));
        }
        catch(IOException e)
        {
            logger_.error("IO error message processing: "+ f.getName()+"\n"+MyClassUtils.getStackTrace(e));
        }

        return null;
    }//processMIME			

    //////////////////////////////////////////////HELPERS/////////////////////////////////////////////////	
	
	
	
	
//	//sample usage
//	public static void main(String[] args) throws Exception
//	{
//		MailParser mp=new MailParser("C:\\Documents and Settings\\paxosk\\workspace2\\properties\\");
//		mp.storeMsgFilenames();
//		
//		while (mp.hasNextMessage())
//		{
//                    MimeMessage msg=mp.getNextMessage();
//                    Enumeration<Header> msgenum=msg.getAllHeaders();
//                    
//                    while (msgenum.hasMoreElements())
//                    {
//                        Header h=msgenum.nextElement();
//                        System.err.println("header_name: "+h.getName()+"\t"+
//                                "header_value: "+h.getValue());
//                    }
//                    System.err.println("====================================");
//		}
//	}//main
}
