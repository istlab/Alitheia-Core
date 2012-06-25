package eu.sqooss.plugins.javaparser;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.antlr.runtime.ANTLRInputStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.tree.Tree;

import eu.sqooss.parsers.java.ASTWalker;
import eu.sqooss.parsers.java.CodeFragment;
import eu.sqooss.parsers.java.EntityExtractor;
import eu.sqooss.parsers.java.JavaTreeLexer;
import eu.sqooss.parsers.java.JavaTreeParser;
import eu.sqooss.parsers.java.SpanningNodeAdaptor;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.EncapsulationUnit;
import eu.sqooss.service.db.ExecutionUnit;
import eu.sqooss.service.db.Language;
import eu.sqooss.service.db.NameSpace;
import eu.sqooss.service.db.ProjectFile;
import eu.sqooss.service.db.ProjectVersion;
import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.fds.FDSService;
import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.scheduler.Job;
import eu.sqooss.service.tds.Diff;
import eu.sqooss.service.tds.DiffChunk;
import eu.sqooss.service.tds.InvalidAccessorException;
import eu.sqooss.service.tds.InvalidProjectRevisionException;
import eu.sqooss.service.tds.InvalidRepositoryException;
import eu.sqooss.service.tds.SCMAccessor;

public class JavaUpdaterJob extends Job {

    StoredProject sp;
    ProjectVersion pv;
    Logger log;
    DBService db;
    float progress = 0;
    Pattern functionname = Pattern.compile("^.*::([a-zA-Z0-9:\\[\\]\\,\\ ]*)$");
    
    public JavaUpdaterJob(StoredProject sp, 
            ProjectVersion pv, Logger log) {
        this.sp = sp;
        this.pv = pv;
        this.log = log;
        db = AlitheiaCore.getInstance().getDBService();
    }
    
    @Override
    public long priority() {
        return 0;
    }

    @Override
    protected void run() throws Exception {
        db.startDBSession();
        sp = db.attachObjectToDBSession(sp);
        pv = db.attachObjectToDBSession(pv);
        Pattern p = Pattern.compile(".*\\.java$");
        FDSService fds = AlitheiaCore.getInstance().getFDSService();

        debug("Parsing files in version ", pv.toString());
        Set<ProjectFile> files = pv.getVersionFiles(p);
        int processed = 0;
        
        for (ProjectFile pf : files) {
            if (pf.getIsDirectory() || pf.isDeleted())
                continue;
            debug("Parsing file ", pf.toString());
            processed++;
            progress = (float) (((double)processed / (double)files.size()) * 100);
            
            if (pf.isDeleted() || pf.getIsDirectory() == true)
                continue;

            Long ts = System.currentTimeMillis();
            InputStream is = fds.getFileContents(pf);

            if (is == null) {
                err("Null contents for file ", pf.toString());
                continue;
            }
            // Parse the input file
            Tree t = null;
            JavaTreeLexer lexer = null;
            try {
                ANTLRInputStream input = new ANTLRInputStream(is);
                lexer = new JavaTreeLexer(input);
                CommonTokenStream tokens = new CommonTokenStream(lexer);
                JavaTreeParser parser = new JavaTreeParser(tokens);
                SpanningNodeAdaptor adaptor = new SpanningNodeAdaptor();
                parser.setTreeAdaptor(adaptor);

                JavaTreeParser.compilationUnit_return result =
                        parser.compilationUnit();
                t = (Tree) result.getTree();
            } catch (Exception e) {
                warn("Cannot parse file ", pf.toString(), ": ", e.getMessage());
            } finally {

                debug("parseSource(", pf.toString() ,"): ", String.valueOf(System.currentTimeMillis() - ts),
                        " ms, lines: ", String.valueOf(lexer.getLine()));
            }

            // Walk resulting tree
            ASTWalker walker = new ASTWalker();
            EntityExtractor ee = new EntityExtractor();
            walker.addProcessor(ee);
            walker.walk(t);

            NameSpace ns = NameSpace.findByVersionName(pf.getProjectVersion(), 
                    ee.getPackageName());
            
            if (ns == null) {
                ns = new NameSpace();
                ns.setName(ee.getPackageName());
                ns.setChangeVersion(pf.getProjectVersion());
                ns.setLang(Language.JAVA);
                db.addRecord(ns);
            }
            
            for (String clazz : ee.getResults().keySet()) {
                List<String> changedMethods = getChangedMethods(ee, pf, clazz);
                EncapsulationUnit eu = new EncapsulationUnit(pf);
                eu.setName(clazz);
                eu.setNamespace(ns);
                eu.setFile(pf);
                db.addRecord(eu);

                for (CodeFragment fragment : ee.getResults().get(clazz)) {
                    ExecutionUnit exu = new ExecutionUnit(eu);
                    exu.setName(getMethodName(fragment, pf));
                    exu.setFile(pf);
                    exu.setNamespace(ns);
                    exu.setEncapsulationUnit(eu);

                    if (changedMethods.contains(fragment.getFullyQualifiedName())) {
                        debug("Method " , fragment.toString() , " changed in rev ", 
                                pf.getProjectVersion().toString());
                        exu.setChanged(true);
                    }
                    db.addRecord(exu);
                }
            }
        }
        db.commitDBSession();
    }
    
    private List<String> getChangedMethods(EntityExtractor ee, ProjectFile pf, 
            String clazz) 
        throws InvalidAccessorException, InvalidProjectRevisionException, 
               InvalidRepositoryException, FileNotFoundException {
        Long ts = System.currentTimeMillis();
        List<String> changedMethods = new ArrayList<String>();
        ProjectFile prev = pf.getPreviousFileVersion();
        
        if (prev == null) {
            if (!pf.isAdded())
                warn("Cannot find previous version for file ", pf.toString());
                
            for (CodeFragment method : ee.getResults().get(clazz)) {
                
                if (method.getFullyQualifiedName() == null) {
                    warn("Name from fragment [" + method.getStartLine() + ","
                            + method.getEndLine() + "] in file: " + pf + " is null");
                    continue; //TODO: This is a bug
                }
                
                if (!method.getFullyQualifiedName().contains("::"))
                    continue; //Class fragment
            
                changedMethods.add(method.getFullyQualifiedName());
            }
            return changedMethods;
        }

        SCMAccessor scm = AlitheiaCore.getInstance().getTDSService().getAccessor(
                pf.getProjectVersion().getProject().getId()).getSCMAccessor();
        
        Diff diff = scm.getDiff(pf.getFileName(),
                scm.newRevision(prev.getProjectVersion().getRevisionId()),
                scm.newRevision(pf.getProjectVersion().getRevisionId()));

        for (DiffChunk d : diff.getDiffChunks().get(pf.getFileName())) {
            for (CodeFragment fragment : ee.getResults().get(clazz)) {

                if (fragment.getFullyQualifiedName() == null) {
                    warn("Name from fragment [" + fragment.getStartLine() + ","
                            + fragment.getEndLine() + "] in file: " + pf + " is null");
                    continue; //TODO: This is a bug
                }
                
                if (!fragment.getFullyQualifiedName().contains("::"))
                    continue; // Class fragment

                if (d.getTargetStartLine() > fragment.getStartLine()
                        && d.getTargetStartLine() < fragment.getEndLine()) {
                    changedMethods.add(fragment.getFullyQualifiedName());
                }
            }
        }
        debug("getChangedMethods(): ", String.valueOf(System.currentTimeMillis() - ts), " ms");
        return changedMethods;
    }
    
    public String getMethodName(CodeFragment fragment, ProjectFile pf) {
        String fullyQualifiedName = fragment.getFullyQualifiedName();
        if (fullyQualifiedName == null) {
                warn("Name from fragment [" + fragment.getStartLine() + ","
                        + fragment.getEndLine() + "] in file: " + pf + " is null");
                return null; //TODO: This is a bug
        }
        
        int idx = fullyQualifiedName.lastIndexOf(":");
        if (idx <= 0)
            return "";
        
        Matcher m = functionname.matcher(fullyQualifiedName);
        String funcDecl = "";
        if (m.matches()) {
            funcDecl = m.group(1);
        }
        
        if (funcDecl.length() > 256)
            funcDecl = funcDecl.substring(0, 255);
        
        return funcDecl;
    }
    
    protected void warn(String...strings) {
        log.warn(getMsg(strings));
    }

    protected void err(String...strings) {
        log.error(getMsg(strings));
    }

    protected void info(String...strings) {
        log.info(getMsg(strings));
    }

    protected void debug(String...strings) {
        if (log != null)
            log.debug(getMsg(strings));
        else
            System.err.println(getMsg(strings));
    }
    
    private String getMsg(String...strings) {
        StringBuffer b = new StringBuffer();
        b.append("JavaUpdater:").append(pv).append(":");
        for (String str : strings) {
            b.append(str);
        }
        return b.toString();
    }
    
    @Override
    public String toString() {
        return "JavaUpdaterJob - Version:{" + pv + "}, " + progress + "%";
    }
}
