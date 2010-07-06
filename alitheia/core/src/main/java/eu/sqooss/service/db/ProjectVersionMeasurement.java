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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Instances of this class represent the result of measurements made
 * against ProjectVersions as stored in the database
 */
@Entity
@Table(name="PROJECT_VERSION_MEASUREMENT")
@XmlRootElement(name="version-measurement")
public class ProjectVersionMeasurement extends MetricMeasurement {
	
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "PROJECT_VERSION_MEASUREMENT_ID")
    @XmlElement(name = "id")
	private long id; 

    /**
     * The metric to which this result belongs
     */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="METRIC_ID", referencedColumnName="METRIC_ID")
    private Metric metric;

    /**
     * A representation of the calculation result
     */
    @Column(name="RESULT")
    private String result;

	/**
     * The ProjectVersion to which the instance relates
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PROJECT_VERSION_ID")
    private ProjectVersion projectVersion;

    public ProjectVersionMeasurement() {
        super();
    }

    /**
     * Convenience constructor that sets all of the fields in the
     * measurement at once, saving a few (explicit) method calls. 
     * @param m Metric the measurement is for
     * @param p Project version the metric was applied to
     * @param v Resulting value
     */
    public ProjectVersionMeasurement(Metric m, ProjectVersion p, String v) {
        this();
        setMetric(m);
        setProjectVersion(p);
        setResult(v);
    }
    
    public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
    
    public ProjectVersion getProjectVersion() {
        return projectVersion;
    }

    public void setProjectVersion(ProjectVersion projectVersion) {
        this.projectVersion = projectVersion;
    }
    
    /**
     * @return the metric
     */
    public Metric getMetric() {
        return metric;
    }
    
    /**
     * @param metric the metric to set
     */
    public void setMetric(Metric metric) {
        this.metric = metric;
    }
    
    /**
     * @return the result
     */
    public String getResult() {
        return result;
    }
    
    /**
     * @param result the result to set
     */
    public void setResult(String result) {
        this.result = result;
    }
}

//vi: ai nosi sw=4 ts=4 expandtab
