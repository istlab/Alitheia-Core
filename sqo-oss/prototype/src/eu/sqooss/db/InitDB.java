package eu.sqooss.db;

import org.hibernate.Session;

import eu.sqooss.util.HibernateUtil;

/**
 * Initializes the database with the default values (invoke only once)
 */
public class InitDB {
    private static MetricType createMetricType(String type) {
        MetricType mt = new MetricType();
        mt.setType(type);

        return mt;
    }

    private static Plugin createPlugin(String name, String description,
            String executor, String executorType, String parser,
            String parserType, String path) {
        Plugin p = new Plugin();

        p.setName(name);
        p.setDescription(description);
        p.setExecutor(executor);
        p.setExecutorType(executorType);
        p.setParser(parser);
        p.setParserType(parserType);
        p.setPath(path);

        return p;
    }

    private static Metric createMetric(String name, String description,
            MetricType metricType, Plugin plugin) {
        Metric metric = new Metric();

        metric.setDescription(description);
        metric.setName(name);
        metric.setPlugin(plugin);
        metric.setMetricType(metricType);

        return metric;
    }

    public static void main(String[] args) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();

        session.beginTransaction();

        // add metric_type
        MetricType codeMetric = createMetricType("Code Metric");
        MetricType statMeasure = createMetricType("Statistical measure");

        session.save(codeMetric);
        session.save(statMeasure);

        // add plugins
        Plugin wcPlugin = createPlugin("wc", "Word Count Plugin", "wc -l %s",
                "external", "eu.sqooss.plugin.wordcount.WCParser", "Java", ""); // path
                                                                                // is
                                                                                // empty

        session.save(wcPlugin);

        Plugin ccccPlugin = createPlugin("cccc", "C/C++ Code Counter Plugin",
                "eu.sqooss.plugin.cccc.CCCCPlugin", "JavaPlugin", "", // no
                                                                        // parser
                                                                        // required
                                                                        // for
                                                                        // JavaPlugins
                "", "");

        session.save(ccccPlugin);

        // add metrics

        session.save(createMetric("WC", "Word Count", codeMetric, wcPlugin));
        session
                .save(createMetric("MVG", "McCabe's CC", codeMetric, ccccPlugin));
        session.save(createMetric("WMC", "Weighted Methods per Class",
                codeMetric, ccccPlugin));
        session.save(createMetric("DIT", "Depth of Inheritance Tree",
                codeMetric, ccccPlugin));
        session.save(createMetric("NOC", "Number of Children", codeMetric,
                ccccPlugin));
        session.save(createMetric("CBO", "Coupling Between Objects",
                codeMetric, ccccPlugin));
        // finally commit
        session.getTransaction().commit();

        // close the session
        HibernateUtil.getSessionFactory().close();
    }
}
