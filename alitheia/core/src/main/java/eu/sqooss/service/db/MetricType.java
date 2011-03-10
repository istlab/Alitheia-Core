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
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import eu.sqooss.core.AlitheiaCore;

/**
 * Instances of this class represent to what forms of data a metric
 * stored in the database is related to
 * 
 * @assoc 1 - n Metric
 */
@Entity
@Table(name="METRIC_TYPE")
@XmlRootElement(name="metrictype")
public class MetricType extends DAObject {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="METRIC_TYPE_ID")
    @XmlElement
    private long id; 

    /**
     * A string representation of the type of metric
     */
	@XmlElement
	@Column(name="type")
    private String type;

    /**
     * A list of all metrics of this type
     */
	@OneToMany(mappedBy="metricType")
    private Set<Metric> metrics;

	/**
	 * An enumeration of all possible metric types. Metric types map to
	 * activation types, but not necessarily on a 1-1 basis.
	 */
    public enum Type {

        PROJECT,
        SOURCE_FILE,
        SOURCE_DIRECTORY, 
        MAILING_LIST,
        BUG, PROJECT_VERSION,
        MAILTHREAD, MAILMESSAGE,
        DEVELOPER, NAMESPACE,
        EXECUNIT, ENCAPSUNIT;

        public static Type fromString(String s) {
            if ("SOURCE_CODE".equals(s) || "SOURCE_FILE".equals(s))
                return Type.SOURCE_FILE;
            if ("SOURCE_FOLDER".equals(s) || "SOURCE_DIRECTORY".equals(s))
                return Type.SOURCE_DIRECTORY;
            else if ("MAILING_LIST".equals(s))
                return Type.MAILING_LIST;
            else if ("BUG_DATABASE".equals(s) || "BUG".equals(s))
                return Type.BUG;
            else if ("THREAD".equals(s) || "MAILTHREAD".equals(s))
                return Type.MAILTHREAD;
            else if ("MAILMESSAGE".equals(s))
                return Type.MAILMESSAGE;
            else if ("PROJECT_WIDE".equals(s) || "PROJECT_VERSION".equals(s))
                return Type.PROJECT_VERSION;
            else if ("DEVELOPER".equals(s))
            	return Type.DEVELOPER;
            else if ("NAMESPACE".equals(s))
                return Type.NAMESPACE;
            else if ("EXECUNIT".equals(s))
                return Type.EXECUNIT;
            else if ("ENCAPSUNIT".equals(s))
                return Type.ENCAPSUNIT;
            else
                return null;
        }
    }

    public MetricType() {
        // Nothing to do here
    }

    public MetricType(Type t) {
        type = t.toString();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
    
    public Type getEnumType() {
        return Type.fromString(type);
    }

    public String getType() {
        return type;
    }
    
    public void setEnumType(Type type) {
        this.type = type.toString();
    }

    public void setType(String s) {
        this.type = Type.fromString(s).toString();
    }
    
    public Set<Metric> getMetrics() {
        return metrics;
    }

    public void setMetrics(Set<Metric> metrics) {
        this.metrics = metrics;
    }

    /**
     * Get the corresponding DAO for the provided metric type.
     * 
     * @return A MetricType DAO representing the metric type
     */
    public static MetricType getMetricType(Type t) {
        DBService db = AlitheiaCore.getInstance().getDBService();
        HashMap<String, Object> s = new HashMap<String, Object>();
        s.put("type", t.toString());
        List<MetricType> result = db.findObjectsByProperties(MetricType.class, s);
        if (result.isEmpty()) {
            return null;
        }
        else {
            return result.get(0);
        }
    }

    /**
     * Single point of truth for conversions between the activation types 
     * known to plug-ins and metric types used internally.
     */
	public static MetricType.Type fromActivator(Class<? extends DAObject> activator) {
	    
	   if (activator.equals(ProjectFile.class))
	       return Type.SOURCE_FILE;
	   if (activator.equals(ProjectDirectory.class))
           return Type.SOURCE_DIRECTORY;
	   if (activator.equals(ProjectVersion.class))
	       return Type.PROJECT_VERSION;
	   if (activator.equals(StoredProject.class))
	       return Type.PROJECT;
	   if (activator.equals(MailingList.class))
	       return Type.MAILING_LIST;
	   if (activator.equals(MailMessage.class))
	       return Type.MAILMESSAGE;
	   if (activator.equals(MailingListThread.class))
	       return Type.MAILTHREAD;
	   if (activator.equals(Bug.class))
	       return Type.BUG;
	   if (activator.equals(Developer.class))
		   return Type.DEVELOPER;
	   if (activator.equals(NameSpace.class))
           return Type.NAMESPACE;
	   if (activator.equals(EncapsulationUnit.class))
           return Type.ENCAPSUNIT;
	   if (activator.equals(ExecutionUnit.class))
           return Type.EXECUNIT;
	   return null;
	}
	
	public Class<? extends DAObject> toActivator() {
	    switch(Type.fromString(this.type)) {
	        case SOURCE_DIRECTORY:
	            return ProjectDirectory.class;
	        case SOURCE_FILE:
	            return ProjectFile.class;
	        case PROJECT_VERSION:
	            return ProjectVersion.class;
	        case PROJECT:
	            return StoredProject.class;
	        case MAILMESSAGE:
	            return MailMessage.class;
	        case MAILING_LIST:
	            return MailingList.class;
	        case MAILTHREAD:
	            return MailingListThread.class;
	        case BUG:
	            return Bug.class;
	        case DEVELOPER:
	        	return Developer.class;
	        case NAMESPACE:
	            return NameSpace.class;
	        case ENCAPSUNIT:
	            return EncapsulationUnit.class;
	        case EXECUNIT:
	            return ExecutionUnit.class;
	    }
	    return null;
	}
}

//vi: ai nosi sw=4 ts=4 expandtab
