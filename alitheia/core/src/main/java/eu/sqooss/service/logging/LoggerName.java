package eu.sqooss.service.logging;

public enum LoggerName {

	/**
     * Represents SQO-OSS system logger name.
     */
	SQOOSS("sqooss"),
	
	/**
     * Represents service system logger name.
     */
	SERVICE("sqooss.service"),
	
	/**
     * Represents database connectivity logger name.
     */
	DB("sqooss.db"),
	
	/**
     * Represents security logger name.
     */
	SECURITY("sqooss.security"),
	
	/**
     * Represents messaging logger name.
     */
	MESSAGING("sqooss.messaging"),
	
	/**
     * Represents web services logger name.
     */
	WEBSERVICE("sqooss.webservice"),
	
	/**
     * Represents scheduling logger name.
     */
	SCHEDULER("sqooss.scheduler"),
	
	/**
     * Represents updater logger name.
     */
	UPDATER("sqooss.updater"),
	
	 /**
     * Represents clusternode logger name.
     */
	CLUSTER("sqooss.cluster"),
	
	/**
     * Represents web UI logger name.
     */
	WEBADMIN("sqooss.webadmin"),
	
	/**
     * Represents TDS logger name.
     */
	TDS("sqooss.tds"),
	
	/**
     * Represents FDS logger name.
     */
	FDS("sqooss.fds"),
	
	/**
     * Represents PluginAdmin logger name.
     */
	PA("sqooss.pa"),
	
	/**
     * Represents Metric logger name.
     */
	METRIC("sqooss.metric"),
	
	/**
     * Represents Metric logger name.
     */
	TESTER("sqooss.tester"),
	
	/**
     * Represents Metric logger name.
     */
	METRICACTIVATOR("sqooss.metricactivator"),
	
	/**
     * Represents Parser logger name.
     */
	PARSER("sqooss.parser"),
	
	/**
     * Represents Parser logger name.
     */
	ADMINACTION("sqooss.adminaction"),
	
	/**
	 * Represents Jobtimer logger name.
	 */
	JOBTIMER("sqooss.jobtimer"),
	
	/**
	 * Represents AdminActionBase logger name.
	 */
	ADMIN("sqooss.admin"),
	
	/**
	 * Represents RestService logger name.
	 */
	REST("sqooss.rest");
	
	private String name;
	
	LoggerName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}
}
