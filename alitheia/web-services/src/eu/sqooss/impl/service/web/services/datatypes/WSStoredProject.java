/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007-2008 by the SQO-OSS consortium members <info@sqo-oss.eu>
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

package eu.sqooss.impl.service.web.services.datatypes;

import java.util.List;

import eu.sqooss.service.db.StoredProject;

/**
 * This class wraps the <code>eu.sqooss.service.db.StoredProject</code>.
 */
public class WSStoredProject {
    
    private long id;
    private String bugs;
    private String contact;
    private String mail;
    private String name;
    private String repository;
    private String website;

    /**
     * @return the bugs
     */
    public String getBugs() {
        return bugs;
    }

    /**
     * @param bugs the bugs to set
     */
    public void setBugs(String bugs) {
        this.bugs = bugs;
    }

    /**
     * @return the contact
     */
    public String getContact() {
        return contact;
    }

    /**
     * @param contact the contact to set
     */
    public void setContact(String contact) {
        this.contact = contact;
    }

    /**
     * @return the id
     */
    public long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * @return the mail
     */
    public String getMail() {
        return mail;
    }

    /**
     * @param mail the mail to set
     */
    public void setMail(String mail) {
        this.mail = mail;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the repository
     */
    public String getRepository() {
        return repository;
    }

    /**
     * @param repository the repository to set
     */
    public void setRepository(String repository) {
        this.repository = repository;
    }

    /**
     * @return the website
     */
    public String getWebsite() {
        return website;
    }

    /**
     * @param website the website to set
     */
    public void setWebsite(String website) {
        this.website = website;
    }
    
    /**
     * The method creates a new <code>WSStoredProject</code> object
     * from the existent DAO object.
     * The method doesn't care of the db session. 
     * 
     * @param storedProject - DAO stored project object
     * 
     * @return The new <code>WSStoredProject</code> object
     */
    public static WSStoredProject getInstance(StoredProject storedProject) {
        if (storedProject == null) return null;
        try {
            WSStoredProject wsStoredProject = new WSStoredProject();
            wsStoredProject.setId(storedProject.getId());
            wsStoredProject.setBugs(storedProject.getBugs());
            wsStoredProject.setContact(storedProject.getContact());
            wsStoredProject.setMail(storedProject.getMail());
            wsStoredProject.setName(storedProject.getName());
            wsStoredProject.setRepository(storedProject.getRepository());
            wsStoredProject.setWebsite(storedProject.getWebsite());
            return wsStoredProject;
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * The method returns an array containing
     * all of the elements in the stored projects list.
     * The list argument should contain DAO
     * <code>StoredProject</code> objects.
     * The method doesn't care of the db session.
     *  
     * @param storedProjects - the stored projects list;
     * the elements should be <code>StoredProject</code> objects  
     * 
     * @return - an array with <code>WSStoredProject</code> objects;
     * if the list is null, contains different object type
     * or the DAO can't be wrapped then the array is null
     */
    public static WSStoredProject[] asArray(List<?> storedProjects) {
        WSStoredProject[] result = null;
        if (storedProjects != null) {
            result = new WSStoredProject[storedProjects.size()];
            StoredProject currentStoredProject;
            WSStoredProject currentWSStoredProject;
            for (int i = 0; i < result.length; i++) {
                try {
                    currentStoredProject = (StoredProject) storedProjects.get(i);
                } catch (ClassCastException e) {
                    return null;
                }
                currentWSStoredProject = WSStoredProject.getInstance(currentStoredProject);
                if (currentWSStoredProject == null) return null;
                result[i] = currentWSStoredProject;
            }
        }
        return result;
    }
    
}

//vi: ai nosi sw=4 ts=4 expandtab
