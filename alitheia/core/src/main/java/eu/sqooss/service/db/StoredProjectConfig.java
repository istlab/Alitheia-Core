/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2009 - 2010 - Organization for Free and Open Source Software,  
 *                 Athens, Greece.
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

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.annotations.NaturalId;

@XmlRootElement(name="project-config")
@Entity
@Table(name="STORED_PROJECT_CONFIG")
class StoredProjectConfig extends DAObject {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="STORED_PROJECT_CONFIG_ID")
	private long id;

	@NaturalId
	@Column(name="STORED_PROJECT_CONFIG_OPTION")
	@Enumerated(EnumType.STRING)
	private ConfigOption confOpt;

	@NaturalId
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="STORED_PROJECT_ID")
	// Don't cascade changes, as we are never used directly
	private StoredProject project;

	@ElementCollection(fetch = FetchType.LAZY)
	@CollectionTable(name="STORED_PROJECT_CONFIG_VALUES")
	@Column(name="VALUE")
	@XmlElement(name="value")
	private Set<String> values = new HashSet<>();

	public StoredProjectConfig() {}
	
	public StoredProjectConfig(ConfigOption co, Set<String> values,
			StoredProject sp) {
		this.confOpt = co;
		this.values = values;
		this.project = sp;
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
	public ConfigOption getConfOpt() {
		return confOpt;
	}
	
	public void setConfOpt(ConfigOption confOpt) {
		this.confOpt = confOpt;
	}
	
	public StoredProject getProject() {
		return project;
	}
	
	public void setProject(StoredProject project) {
		this.project = project;
	}
	
	public Set<String> getValues() {
		return values;
	}
	
	public void setValues(Set<String> value) {
		this.values = value;
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj instanceof StoredProjectConfig
				&& ((StoredProjectConfig) obj).getConfOpt().equals( getConfOpt() )
				&& ((StoredProjectConfig) obj).getProject().equals( getProject() );
	}

	@Override
	public int hashCode() {
		return Objects.hash(getConfOpt(), getProject());
	}
}
