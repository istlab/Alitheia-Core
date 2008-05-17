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

package eu.sqooss.scl.accessor;

import eu.sqooss.scl.WSException;
import eu.sqooss.ws.client.datatypes.WSFileGroup;
import eu.sqooss.ws.client.datatypes.WSProjectFile;
import eu.sqooss.ws.client.datatypes.WSProjectVersion;
import eu.sqooss.ws.client.datatypes.WSStoredProject;

/**
 * This class contains the projects methods. 
 */
public abstract class WSProjectAccessor extends WSAccessor {
    
    /**
     * This method returns an array of all projects accessible from the given
     * user, that the SQO-OSS framework has had evaluated.
     * 
     * @return The array of evaluated projects, or a empty array
     * when none are found.
     * 
     * @throws WSException
     * <ul>
     *  <li>if the connection can't be establish to the SQO-OSS's web services service</li>
     *  <li>if web services service throws a exception</li>
     * <ul>
     */
    public abstract WSStoredProject[] getEvaluatedProjects() throws WSException;
    
    /**
     * This method returns an array of all projects accessible from the given
     * user, no matter if the SQO-OSS framework had evaluated them or not.
     * 
     * @return The array of stored projects, or a empty array when
     * none are found.
     * 
     * @throws WSException
     * <ul>
     *  <li>if the connection can't be established to the SQO-OSS's web services service</li>
     *  <li>if web services service throws an exception</li>
     * <ul>
     */
    public abstract WSStoredProject[] getStoredProjects() throws WSException;
    
    /**
     * This method returns an array of all files that belongs to the project
     * with the given Id.
     * 
     * @param projectId - the project's identifier
     * 
     * @throws WSException
     * <ul>
     *  <li>if the connection can't be established to the SQO-OSS's web services service</li>
     *  <li>if web services service throws an exception</li>
     * <ul>
     */
    public abstract WSProjectFile[] getFilesByProjectId(long projectId) throws WSException;

    /**
     * The method returns an array of all files that exists in the specified
     * project version.
     * 
     * @param projectVersionId - the project's version identifier
     * 
     * @return The array of project's files in that project version,
     * or a empty array when none are found.
     * 
     * @throws WSException
     * <ul>
     *  <li>if the connection can't be established to the SQO-OSS's web services service</li>
     *  <li>if web services service throws an exception</li>
     * <ul>
     */
    public abstract WSProjectFile[] getFilesByProjectVersionId(long projectVersionId) throws WSException;
    
    /**
     * This method returns an array of all file groups that belongs to the project
     * with the given Id.
     * 
     * @param userName - the user's name used for authentication
     * @param password - the user's password used for authentication
     * @param projectId - the project's identifier
     * 
     * @return The array of project's file groups, or a <code>empty</code> array when
     *   none are found <i>(for example, when the project is not yet not
     *   evaluated)</i>.
     *   
     * @throws WSException
     * <ul>
     *  <li>if the connection can't be established to the SQO-OSS's web services service</li>
     *  <li>if web services service throws an exception</li>
     * <ul>
     */
    public abstract WSFileGroup[] getFileGroupsByProjectId(long projectId) throws WSException;
    
    /**
     * The method returns the total number of files, that exists in the given
     * project version.
     * 
     * @param projectVersionId - the project's version identifier
     * 
     * @return The number of project's files in that project version.
     * 
     * @throws WSException
     * <ul>
     *  <li>if the connection can't be established to the SQO-OSS's web services service</li>
     *  <li>if web services service throws an exception</li>
     * <ul>
     */
    public abstract long getFilesNumberByProjectVersionId(long projectVersionId) throws WSException;
    
    /**
     * The method returns the total of the files, that exist in the given project.
     * 
     * @param userName - the user's name used for authentication
     * @param password - the user's password used for authentication
     * @param projectId - the project's identifier
     * 
     * @return The number of the eproject's files.
     * 
     * @throws WSException
     * <ul>
     *  <li>if the connection can't be established to the SQO-OSS's web services service</li>
     *  <li>if web services service throws an exception</li>
     * <ul>
     */
    public abstract long getFilesNumberByProjectId(long projectId) throws WSException;
    
    /**
     * The method returns all information, that the SQO-OSS framework has
     * collected about the specified project.
     * 
     * @param projectName - the project's name
     * 
     * @return The <code>WSStoredProject</code> object that describes the
     * project, or <code>null</code> when such project does not exist.
     * 
     * @throws WSException
     * <ul>
     *  <li>if the connection can't be established to the SQO-OSS's web services service</li>
     *  <li>if web services service throws an exception</li>
     * <ul>
     */
    public abstract WSStoredProject getProjectByName(String projectName) throws WSException;
    
    /**
     * The method returns an array representing all evaluated versions of the
     * given project.
     * 
     * @param projectId - the project's identifier
     * 
     * @return The array with all evaluated project versions,
     * or a empty array when none are found.
     * 
     * @throws WSException
     * <ul>
     *  <li>if the connection can't be established to the SQO-OSS's web services service</li>
     *  <li>if web services service throws an exception</li>
     * <ul>
     */
    public abstract WSProjectVersion[] getProjectVersionsByProjectId(long projectId) throws WSException;
    
    /**
     * The method returns all information, that the SQO-OSS framework has
     * collected about the specified project.
     * 
     * @param projectId - the project's identifier
     * 
     * @return The <code>WSStoredProject</code> object that describes the
     * project, or <code>null</code> when such project does not exist.
     * 
     * @throws WSException
     * <ul>
     *  <li>if the connection can't be established to the SQO-OSS's web services service</li>
     *  <li>if web services service throws an exception</li>
     * <ul>
     */
    public abstract WSStoredProject getProjectById(long projectId) throws WSException;
    
}

//vi: ai nosi sw=4 ts=4 expandtab
