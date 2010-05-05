/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007 - 2010 - Organization for Free and Open Source Software,  
 *                Athens, Greece.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *
 *     * Redistributions in binary form must reproduce the above
 *       copyright notice, this list of conditions and the following
 *       disclaimer in the documentation and/or other materials provided
 *       with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package eu.sqooss.service.db;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import eu.sqooss.core.AlitheiaCore;

/**
 * Instances of this object type represent the basic information on Metrics
 * stored in the database
 * 
 * @assoc 1 - n MetricMeasurement
 * @assoc 1 - n ProjectVersionMeasurement
 * @assoc 1 - n StoredProjectMeasurement
 * @assoc 1 - n ProjectFileMeasurement
 * @assoc 1 - n FileGroupMeasurement
 * 
 */
@XmlRootElement(name="metric")
public class Metric extends DAObject {
	/**
	 * the Alitheia Core plugin providing the functionality for this metric
	 */
	private Plugin plugin;

	/**
	 * A representation of the type of metric: SOURCE_CODE - Relating to SVN
	 * source files SOURCE_FOLDER - Relating to SVN source folders MAILING_LIST
	 * - Relating to email data BUG_DATABASE - Relating to BTS data PROJECT_WIDE
	 * - Relating to all available project data
	 */
	@XmlElement(name="metrictype")
	private MetricType metricType;

	/**
	 * The short form of the metric's name
	 */
	@XmlElement
	private String mnemonic;

	/**
	 * A description of the work performed by this metric
	 */
	@XmlElement
	private String description;

	/**
	 * A list of project-wide measurements for this metric
	 */
	private Set<StoredProjectMeasurement> projectMeasurements;

	/**
	 * A list of project-version-wide measurements for this metric
	 */
	private Set<ProjectVersionMeasurement> versionMeasurements;

	/**
	 * A list of project-file-wide measurements for this metric
	 */
	private Set<ProjectFileMeasurement> fileMeasurements;

	/**
	 * A list of project-file-group-wide measurements for this metric
	 */
	private Set<FileGroupMeasurement> fileGroupMeasurements;

	public Metric() {
		// Nothing to do here
	}

	public MetricType getMetricType() {
		return metricType;
	}

	public void setMetricType(MetricType metricType) {
		this.metricType = metricType;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Plugin getPlugin() {
		return plugin;
	}

	public void setPlugin(Plugin plugin) {
		this.plugin = plugin;
	}

	public String getMnemonic() {
		return mnemonic;
	}

	public void setMnemonic(String mnemonic) {
		this.mnemonic = mnemonic;
	}

	public Set<StoredProjectMeasurement> getProjectMeasurements() {
		return projectMeasurements;
	}

	public void setProjectMeasurements(
			Set<StoredProjectMeasurement> projectMeasurements) {
		this.projectMeasurements = projectMeasurements;
	}

	public Set<ProjectVersionMeasurement> getVersionMeasurements() {
		return versionMeasurements;
	}

	public void setVersionMeasurements(
			Set<ProjectVersionMeasurement> versionMeasurements) {
		this.versionMeasurements = versionMeasurements;
	}

	public Set<ProjectFileMeasurement> getFileMeasurements() {
		return fileMeasurements;
	}

	public void setFileMeasurements(Set<ProjectFileMeasurement> fileMeasurements) {
		this.fileMeasurements = fileMeasurements;
	}

	public Set<FileGroupMeasurement> getFileGroupMeasurements() {
		return fileGroupMeasurements;
	}

	public void setFileGroupMeasurements(
			Set<FileGroupMeasurement> fileGroupMeasurements) {
		this.fileGroupMeasurements = fileGroupMeasurements;
	}

	/**
	 * Check whether the metric was ever run on the provided project.
	 */
	public boolean isEvaluated (StoredProject p) {
		DBService dbs = AlitheiaCore.getInstance().getDBService();
		StringBuffer query = new StringBuffer();

		switch (metricType.getEnumType()) {
		case PROJECT_VERSION:
			query.append("select pvm from ProjectVersionMeasurement pvm ")
				 .append("where pvm.metric=:metric and pvm.projectVersion.project=:project");
			break;
		case SOURCE_FILE:
		case SOURCE_DIRECTORY:
			query.append("select pfm from ProjectFileMeasurement pfm ")
				 .append("where pfm.metric=:metric ")
				 .append("and pfm.projectFile.projectVersion.project=:project");
			break;
		case MAILTHREAD:
			query.append("select mltm from MailingListThreadMeasurement mltm ")
				 .append("where mltm.metric=:metric ")
				 .append("and mltm.thread.list.storedProject=:project");
			break;
		case MAILMESSAGE:
			query.append("select mmm from MailMessageMeasurement mmm ")
				.append("where mmm.metric=:metric")
				.append("and mmm.mail.list.storedProject=:project");
			break;
		case BUG:
		case MAILING_LIST:
		case DEVELOPER:
			return false; //No DAO result types for those types yet
		}
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("project", p);
		params.put("metric", this);
		
		if (dbs.doHQL(query.toString(), params, 1).size() >= 1)
			return true;
		
		return false;
	}

	@Override
	public boolean equals(Object obj) {
		if ((obj == null) || (!(obj instanceof Metric))) {
			return false;
		}
		Metric anotherMetric = (Metric) obj;
		if (mnemonic == null) {
			return this.getId() == anotherMetric.getId();
		} else {
			return (this.mnemonic.equals(anotherMetric.getMnemonic()));
		}
	}

	@Override
	public int hashCode() {
		if (mnemonic != null) {
			return mnemonic.hashCode();
		} else {
			return Long.valueOf(this.getId()).hashCode();
		}
	}

	public String toString() {
		return "Metric(" + getId() + ",\"" + getMnemonic() + "\")";
	}

	/**
	 * Get a metric from its mnemonic name
	 * 
	 * @param mnem
	 *            - The metric mnemonic name to search for
	 * @return A Metric object or null when no metric can be found for the
	 *         provided mnemonic
	 */
	public static Metric getMetricByMnemonic(String mnem) {
		DBService dbs = AlitheiaCore.getInstance().getDBService();

		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put("mnemonic", mnem);

		List<Metric> result = dbs.findObjectsByProperties(Metric.class,
				properties);

		if (result.size() <= 0)
			return null;

		return result.get(0);
	}

	/**
	 * Get a list of all installed metrics.
	 * 
	 * @return A list of all installed metrics, which might be empty if no
	 *         metric is installed.
	 */
	public static List<Metric> getAllMetrics() {
		DBService dbs = AlitheiaCore.getInstance().getDBService();
		return (List<Metric>) dbs.doHQL("from Metric");
	}
}

// vi: ai nosi sw=4 ts=4 expandtab
