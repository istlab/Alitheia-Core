package eu.sqooss.metrics.java;

import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import eu.sqooss.parsers.java.*;
import eu.sqooss.service.fds.FDSService;
import org.antlr.runtime.ANTLRInputStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.tree.Tree;
import org.osgi.framework.BundleContext;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.service.abstractmetric.AbstractMetric;
import eu.sqooss.service.abstractmetric.MetricDecl;
import eu.sqooss.service.abstractmetric.MetricDeclarations;
import eu.sqooss.service.abstractmetric.Result;
import eu.sqooss.service.abstractmetric.SchedulerHints;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.EncapsulationUnit;
import eu.sqooss.service.db.EncapsulationUnitMeasurement;
import eu.sqooss.service.db.ExecutionUnit;
import eu.sqooss.service.db.ExecutionUnitMeasurement;
import eu.sqooss.service.db.Metric;
import eu.sqooss.service.db.ProjectFile;
import eu.sqooss.service.db.ProjectVersion;
import eu.sqooss.service.scheduler.Job;
import eu.sqooss.service.scheduler.ResumePoint;
import eu.sqooss.service.scheduler.Scheduler;
import eu.sqooss.service.scheduler.SchedulerException;

@MetricDeclarations(metrics = {
  @MetricDecl(mnemonic = "MCCABE", activators = {ExecutionUnit.class, ProjectVersion.class}, descr = "McCabe Complexity Metric"),
  @MetricDecl(mnemonic = "WMC", activators = {EncapsulationUnit.class, ProjectVersion.class}, descr = "Weighted Methods per Class"),
  @MetricDecl(mnemonic = "DIT", activators = {EncapsulationUnit.class, ProjectVersion.class}, descr = "Depth of Inheritance Tree"),
  @MetricDecl(mnemonic = "NOC", activators = {EncapsulationUnit.class, ProjectVersion.class}, descr = "Number of Children"),
  @MetricDecl(mnemonic = "CBO", activators = {EncapsulationUnit.class, ProjectVersion.class}, descr = "Coupling Between Objects"),
  @MetricDecl(mnemonic = "RFC", activators = {EncapsulationUnit.class, ProjectVersion.class}, descr = "Response For a Class"),
  @MetricDecl(mnemonic = "LCOM", activators = {EncapsulationUnit.class, ProjectVersion.class}, descr = "Lack of Cohesion in Methods"),
  @MetricDecl(mnemonic = "NUMM", activators = {EncapsulationUnit.class, ProjectVersion.class}, descr = "Number of Methods"),
  @MetricDecl(mnemonic = "NPM", activators = {EncapsulationUnit.class, ProjectVersion.class}, descr = "Number of Public Methods")
})
@SchedulerHints(activationOrder = {ProjectVersion.class, EncapsulationUnit.class})
public class JavaMetrics extends AbstractMetric {

    private List<ProjectFile> changedFiles;
    private ProjectVersion pv;
    //Class -> Base 
    private ConcurrentMap<String, String> reducer;
    private DBService db;

    public JavaMetrics(BundleContext bc) {
        super(bc);
    }

    public List<Result> getResult(ProjectFile a, Metric m) {
        return null;
    }

    public List<Result> getResult(ProjectVersion a, Metric m) {
        return null;
    }

    public List<Result> getResult(ExecutionUnit a, Metric m) {
        return getResult(a, ExecutionUnitMeasurement.class,
                m, Result.ResultType.INTEGER);
    }

    public List<Result> getResult(EncapsulationUnit a, Metric m) {
        return getResult(a, EncapsulationUnitMeasurement.class,
                m, Result.ResultType.INTEGER);
    }

    public void run(ProjectFile pf) throws Exception {
    }

    public void run(EncapsulationUnit wu) throws Exception {
    }

    public void run(ExecutionUnit eu) throws Exception {
    }

    public void run(ProjectVersion pv) throws Exception {
        db = AlitheiaCore.getInstance().getDBService();
        pv = db.attachObjectToDBSession(pv);
        this.pv = pv;

        Pattern p = Pattern.compile("([^\\s]+(\\.(?i)(java))$)");
        changedFiles = new ArrayList<ProjectFile>();

        for (ProjectFile pf : pv.getVersionFiles()) {
            Matcher m = p.matcher(pf.getName());
            if (m.matches())
                changedFiles.add(pf);
        }

        //No Java files changed, skip parsing
        if (changedFiles.size() == 0) {
            info("No Java files changed, skipping version");
            return;
        }

        reducer = new ConcurrentHashMap<String, String>();
        for (ProjectFile pf : pv.getFiles(p))
        try {
            if(!db.isDBSessionActive()) db.startDBSession();
            parseFile(pf);
        } catch (Exception e) {

        } finally {
            if(db.isDBSessionActive()) db.commitDBSession();
        }


        List<EncapsulationUnit> changedClasses = new ArrayList<EncapsulationUnit>();
        for (ProjectFile pf : changedFiles) {
            pf = db.attachObjectToDBSession(pf);
            changedClasses.addAll(pf.getEncapsulationUnits());
        }

        Metric DIT = Metric.getMetricByMnemonic("DIT");
        Metric NOC = Metric.getMetricByMnemonic("NOC");

        for (EncapsulationUnit clazz : changedClasses) {
            String classname = clazz.getName();
            String base = reducer.get(classname);
            int dit = 1;
            while (!base.equals("java.lang.Object")) {
                base = reducer.get(base);
                dit++;
            }

            int noc = 0;
            for (String value : reducer.values())
                if (value.equals(classname))
                    noc++;

            EncapsulationUnitMeasurement eum = new EncapsulationUnitMeasurement(clazz, DIT, String.valueOf(dit));
            db.addRecord(eum);
            eum = new EncapsulationUnitMeasurement(clazz, NOC, String.valueOf(dit));
            db.addRecord(eum);
        }

        db.commitDBSession();
    }

    protected void parseFile(ProjectFile pf) throws Exception {

        if (pf.getIsDirectory() || pf.isDeleted() ||
                !pf.getName().endsWith(".java")) {
            return;
        }

        FDSService fds = AlitheiaCore.getInstance().getFDSService();

        InputStream in = fds.getFileContents(pf);
        if (in == null) {
            return;
        }

        // Parse the input file
        ANTLRInputStream input = new ANTLRInputStream(in);
        JavaTreeLexer lexer = new JavaTreeLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JavaTreeParser parser = new JavaTreeParser(tokens);
        SpanningNodeAdaptor adaptor = new SpanningNodeAdaptor();
        parser.setTreeAdaptor(adaptor);

        JavaTreeParser.compilationUnit_return result = parser.compilationUnit();

        Tree t = (Tree) result.getTree();

        // Walkers for resulting tree
        ASTWalker walker = new ASTWalker();
        EntityExtractor entityExtractor = new EntityExtractor();
        walker.addProcessor(entityExtractor);
        McCabeCalculator mcCabeCalculator = new McCabeCalculator(entityExtractor);
        walker.addProcessor(mcCabeCalculator);
        InheritanceExtractor inheritanceExtractor = new InheritanceExtractor(entityExtractor);
        walker.addProcessor(inheritanceExtractor);
        LCOMCalculator lcomCalculator = new LCOMCalculator(entityExtractor);
        walker.addProcessor(lcomCalculator);
        CBOCalculator cboCalculator = new CBOCalculator(inheritanceExtractor);
        walker.addProcessor(cboCalculator);
        walker.walk(t);

        //Data for associated classes/methods
        List<ExecutionUnit> methods = pf.getChangedExecutionUnits();
        Set<EncapsulationUnit> classes = pf.getEncapsulationUnits();
        Set<String> foundClasses = entityExtractor.getResults().keySet();

        //Make class graph
        Deque<InheritanceExtractor.ClassInheritance> classInheritance =
                inheritanceExtractor.getResults();

        for (InheritanceExtractor.ClassInheritance ci : classInheritance) {
            reducer.put(ci.className, ci.superClass);
        }

        // LCOM results
        writeClassResults(classes, lcomCalculator.getResults(), Metric.getMetricByMnemonic("LCOM"));

        // CBO results
        writeClassResults(classes, cboCalculator.getResults(), Metric.getMetricByMnemonic("CBO"));

        // WMC + MCCABE results in one go
        Metric m = Metric.getMetricByMnemonic("WMC");
        SortedMap<String, Integer> MCCABEresults = mcCabeCalculator.getResults();
        for (EncapsulationUnit clazz : classes) {
            Integer wmc = 0;

            for (ExecutionUnit method : clazz.getExecUnits()) {
                Integer res = MCCABEresults.get(method.getFullyQualifiedName());

                if (res == null) {
                    warn("Cannot find result for changed method: ", method.toString());
                    continue;
                }
                wmc += res;
                ExecutionUnitMeasurement eum = new ExecutionUnitMeasurement(
                        method, Metric.getMetricByMnemonic("MCCABE"),
                        res.toString());
                db.addRecord(eum);
            }

            EncapsulationUnitMeasurement eum =
                    new EncapsulationUnitMeasurement(clazz, m, wmc.toString());
            db.addRecord(eum);
        }

        // NUMM results
        for (EncapsulationUnit clazz : classes) {
            EncapsulationUnitMeasurement eum =
                    new EncapsulationUnitMeasurement(clazz,
                            Metric.getMetricByMnemonic("NUMM"),
                            clazz.getExecUnits().toString());
            db.addRecord(eum);
        }
    }

    private void writeClassResults(Set<EncapsulationUnit> classes,
                                   Map<String, Integer> results, Metric m) {
        for (EncapsulationUnit clazz : classes) {
            Integer res = results.get(clazz.getName());

            if (res == null) {
                log.warn("Cannot find " + m.getMnemonic() +
                        " result for clazz: " + clazz);
                continue;
            }

            EncapsulationUnitMeasurement eum = new EncapsulationUnitMeasurement(clazz, m, res.toString());
            db.addRecord(eum);
        }
    }

    protected void warn(String... strings) {
        log.warn(getMsg(strings));
    }

    protected void err(String... strings) {
        log.error(getMsg(strings));
    }

    protected void info(String... strings) {
        log.info(getMsg(strings));
    }

    protected void debug(String... strings) {
        if (log != null)
            log.debug(getMsg(strings));
        else
            System.err.println(getMsg(strings));
    }

    private String getMsg(String... strings) {
        StringBuffer b = new StringBuffer();
        b.append("JavaMetrics:").append(pv).append(":");
        for (String str : strings) {
            b.append(str);
        }
        return b.toString();
    }

    @Override
    public String toString() {
        return "JavaMetrics: Version:" + pv;
    }
}
