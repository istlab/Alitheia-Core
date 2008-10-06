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
import java.util.Set;

import eu.sqooss.service.db.Developer;
import eu.sqooss.service.db.MailingList;
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
    private String developers;
    private String mailingLists;

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
     * Returns the Ids of the developers that work on this project.
     * <br/><br/>
     * <b>Note:</b> Using an array of <code>long</code> as a result, instead
     * of the <code>String</code> based workaround is currently impossible,
     * because of a limitation in the used Axis version i.e. it triggers an
     * exception when passing a <code>null<code> or an empty array field.
     * 
     * @return The list of developer Ids.
     */
    public String getDevelopers() {
        return developers;
    }

    /**
     * Sets the list of Ids of the developers that work on this project.
     * 
     * @param ids the list of developer Ids
     */
    public void setDevelopers(long[] ids) {
        if (ids != null) {
            developers = "";
            for (long id: ids)
                developers += id + ";";
        }
    }

    /**
     * Returns the Ids of the mailing lists associated with this project.
     * <br/><br/>
     * <b>Note:</b> Using an array of <code>long</code> as a result, instead
     * of the <code>String</code> based workaround is currently impossible,
     * because of a limitation in the used Axis version i.e. it triggers an
     * exception when passing a <code>null<code> or an empty array field.
     * 
     * @return The list of mailing list Ids.
     */
    public String getMailingLists() {
        return mailingLists;
    }

    /**
     * Sets the list of mailing list Ids, which are associated with this
     * project.
     * 
     * @param ids the list of mailing list Ids
     */
    public void setMailingLists(long[] ids) {
        if (ids != null) {
            mailingLists = "";
            for (long id: ids)
                mailingLists += id + ";";
        }
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
            wsStoredProject.setBugs(storedProject.getBtsUrl());
            wsStoredProject.setContact(storedProject.getContactUrl());
            wsStoredProject.setMail(storedProject.getMailUrl());
            wsStoredProject.setName(storedProject.getName());
            wsStoredProject.setRepository(storedProject.getScmUrl());
            wsStoredProject.setWebsite(storedProject.getWebsiteUrl());
            List<Developer> developers = storedProject.getDevelopers();
            if ((developers != null) && (developers.size() > 0)) {
                int index = 0;
                long[] developerIds = new long[developers.size()];
                for (Developer developer : developers)
                    developerIds[index++] = developer.getId();
                wsStoredProject.setDevelopers(developerIds);
            }
            Set<MailingList> mailingLists = storedProject.getMailingLists();
            if ((mailingLists != null) && (mailingLists.size() > 0)) {
                int index = 0;
                long[] mailingListIds = new long[mailingLists.size()];
                for (MailingList mailingList : mailingLists)
                    mailingListIds[index++] = mailingList.getId();
                wsStoredProject.setMailingLists(mailingListIds);
            }
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
