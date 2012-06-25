package eu.sqooss.parsers.java.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.antlr.runtime.RecognitionException;
import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNProperties;
import org.tmatesoft.svn.core.SVNProperty;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNWCUtil;


/**
 * Utility class to read the complete file history of a Subversion repository.
 *
 */
public class SVNRepoReader {

    public static SVNRepository connect(String url, String name, 
            String password) throws SVNException {
        
       SVNRepository repository = 
           SVNRepositoryFactory.create(SVNURL.parseURIEncoded(url));
       ISVNAuthenticationManager authManager = 
           SVNWCUtil.createDefaultAuthenticationManager(name, password);
       repository.setAuthenticationManager(authManager);
       return repository;
    }
    
    public static List<String> getFiles(SVNRepository repository,
            long revision, String path) 
        throws SVNException {
        
        List<String> results = new LinkedList<String>();
        @SuppressWarnings("rawtypes")
        Collection entries = repository.getDir(path, 
                revision, null, (Collection) null );
        @SuppressWarnings("rawtypes")
        Iterator iterator = entries.iterator();
        while (iterator.hasNext()) {
            SVNDirEntry entry = (SVNDirEntry) iterator.next( );
            if (entry.getKind() == SVNNodeKind.FILE) {
                results.add(path + "/" + entry.getRelativePath());
            } else if (entry.getKind() == SVNNodeKind.DIR) {
                String newPath = path.equals("") 
                    ? entry.getName( ) 
                    : path + "/" + entry.getName();
                results.addAll(SVNRepoReader.getFiles(repository,
                        revision, 
                        newPath));
            }
        }
        return results;
    }
    
    public static void printFile(SVNRepository repository,
            long revision, String path) throws SVNException {
        
            SVNProperties fileProperties = new SVNProperties();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            repository.getFile(path, revision, fileProperties, baos);
            String mimeType = 
                fileProperties.getStringValue(SVNProperty.MIME_TYPE);
            boolean isTextType = SVNProperty.isTextMimeType(mimeType);
            if (isTextType) {
                System.out.println("File contents:");
                System.out.println();
                try {
                    baos.writeTo(System.out);
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            } else {
                System.out.println( "Not a text file." );
            }       
    }
    
    public static InputStream copyFileToInputStream(SVNRepository repository,
            long revision, String path) 
    throws SVNException {
        
        ByteArrayInputStream is = null;
        SVNProperties fileProperties = new SVNProperties();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        repository.getFile(path, revision, fileProperties, baos);
        String mimeType = 
            fileProperties.getStringValue(SVNProperty.MIME_TYPE);
        boolean isTextType = SVNProperty.isTextMimeType(mimeType);
        if (isTextType) {
            is = new ByteArrayInputStream(baos.toByteArray());
        }
        return is;
    }
    
    public static void main(String args[]) throws SVNException, IOException, 
        RecognitionException {
        
        if (args.length != 2) {
            System.err.println("Usage: SVNRepoReader svnurl");
            System.exit(1);
        }
        FSRepositoryFactory.setup();
        String svnURL = args[1];
        SVNRepository repository = SVNRepoReader.connect(svnURL, "", "");
        for (int revision = 0; revision <= repository.getLatestRevision(); 
            revision++) {
            System.out.println("Handling revision " + revision + "...");
            List<String> files =
                SVNRepoReader.getFiles(repository, revision, "");
            for (String fileName : files) {
                if (!fileName.endsWith(".java"))
                    continue;
                System.out.println(fileName);
                InputStream is = 
                    SVNRepoReader.copyFileToInputStream(repository, 
                            revision, 
                            fileName);
                JavaParserRunner javaParserRunner = new JavaParserRunner();
                javaParserRunner.runParser(is);
                javaParserRunner.runEntityExtractor();
                javaParserRunner.runInheritanceExtractor();
                javaParserRunner.runMcCabeCalculator();
                javaParserRunner.runLCOMCalculator();
                javaParserRunner.runCBOCalculator();
                is.close();
            }
        }
    }
}
