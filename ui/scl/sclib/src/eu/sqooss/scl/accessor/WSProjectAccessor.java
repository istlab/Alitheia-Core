/*
 * Copyright 2008 - Organization for Free and Open Source Software,
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

package eu.sqooss.scl.accessor;

import eu.sqooss.scl.WSException;
import eu.sqooss.ws.client.datatypes.WSDeveloper;
import eu.sqooss.ws.client.datatypes.WSDirectory;
import eu.sqooss.ws.client.datatypes.WSFileGroup;
import eu.sqooss.ws.client.datatypes.WSFileModification;
import eu.sqooss.ws.client.datatypes.WSMailMessage;
import eu.sqooss.ws.client.datatypes.WSProjectFile;
import eu.sqooss.ws.client.datatypes.WSProjectVersion;
import eu.sqooss.ws.client.datatypes.WSStoredProject;
import eu.sqooss.ws.client.datatypes.WSTaggedVersion;
import eu.sqooss.ws.client.datatypes.WSVersionStats;

/**
 * This class contains the projects methods.
 *
 * @author Evgeni Grigorov, <tt>(ProSyst Software GmbH)</tt>
 * @author Boryan Yotov, <tt>(ProSyst Software GmbH)</tt>
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
     * The method returns an array of all files that exists in the specified
     * project version. The files' names conform to the regular expression.
     * 
     * @param projectVersionId - the project's version identifier
     * @param regExpr - the regular expression
     * 
     * @return The array of project's files in that project version
     * or a empty array when none are found.
     * 
     * @throws WSException
     * <ul>
     *  <li>if the connection can't be established to the SQO-OSS's web services service</li>
     *  <li>if web services service throws an exception</li>
     * <ul>
     */
    public abstract WSProjectFile[] getFilesByRegularExpression(long projectVersionId, String regExpr) throws WSException;
    
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
     * This method returns all known information about the directories referenced by
     * the given identifiers.
     * 
     * @param directoriesIds - the identifiers of the requested directories
     * 
     * @return The <code>WSDirectory</code> array describing the requested directories.
     * 
     * @throws WSException
     * <ul>
     *  <li>if the connection can't be established to the SQO-OSS's web services service</li>
     *  <li>if web services service throws an exception</li>
     * <ul>
     */
    public abstract WSDirectory[] getDirectoriesByIds(long[] directoriesIds) throws WSException;
    
    /**
     * This method returns all known information about the developers referenced by
     * the given identifiers.
     * 
     * @param developersIds - the identifiers of the requested developers
     * 
     * @return The <code>WSDeveloper</code> array describing the requested developers.
     * 
     * @throws WSException
     * <ul>
     *  <li>if the connection can't be established to the SQO-OSS's web services service</li>
     *  <li>if web services service throws an exception</li>
     * <ul>
     */
    public abstract WSDeveloper[] getDevelopersByIds(long[] developersIds) throws WSException;
    
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
     * The method returns all information, that the SQO-OSS framework has
     * collected about the specified project versions.
     * 
     * @param projectVerionsIds - the project versions' identifiers
     * 
     * @return The <code>WSProjectVersion</code> array that describes the
     * project versions, or empty array when such project versions do not exist.
     * 
     * @throws WSException
     * <ul>
     *  <li>if the connection can't be established to the SQO-OSS's web services service</li>
     *  <li>if web services service throws an exception</li>
     * <ul>
     */
    public abstract WSProjectVersion[] getProjectVersionsByIds(long[] projectVersionsIds) throws WSException;

    /**
     * The method returns an array that contains all tagged versions in the
     * specified project.
     *
     * @param projectId the project's identifier
     *
     * @return The <code>WSTaggedVersion</code> array with all tagged project
     *  versions, or an empty array when none are found.
     *
     * @throws WSException
     * <ul>
     *  <li>if a connection with SQO-OSS's WSS can not be established</li>
     *  <li>if SQO-OSS's WSS threw an exception</li>
     * <ul>
     */
    public abstract WSTaggedVersion[] getTaggedVersionsByProjectId(
            long projectId) throws WSException;

    /**
     * This method retrieves from the attached SQO-OSS framework the list of
     * project versions which carry the given time stamps.
     * 
     * @param projectId - the project identifier
     * @param timestamps - the list of time stamps
     * 
     * @return The array of <code>WSProjectVersion</code> objects for all
     *  matching project versions, or <code>null</code> if none were found.
     * 
     * @throws WSException
     * <ul>
     *  <li>if a connection with the SQO-OSS's web services service can
     *    not be established</li>
     *  <li>if web services service throws an exception</li>
     * <ul>
     */
    public abstract WSProjectVersion[] getProjectVersionsByTimestamps(
            long projectId, long[] timestamps) throws WSException;

    /**
     * This method retrieves from the attached SQO-OSS framework the list of
     * project versions which carry the given SCM version Ids.
     * 
     * @param projectId - the project identifier
     * @param scmIds - the list of SCM version Ids
     * 
     * @return The array of <code>WSProjectVersion</code> objects for all
     *  matching project versions, or <code>null</code> if none were found.
     * 
     * @throws WSException
     * <ul>
     *  <li>if a connection with the SQO-OSS's web services service can
     *    not be established</li>
     *  <li>if web services service throws an exception</li>
     * <ul>
     */
    public abstract WSProjectVersion[] getProjectVersionsByScmIds(
            long projectId, String[] scmIds) throws WSException;

    /**
     * The method returns all information, that the SQO-OSS framework has
     * collected about the first (e.g. SVN revision 1) of the projects.
     * 
     * @param projectsIds - the projects' identifiers
     * 
     * @return The <code>WSProjectVersion</code> array that describes the
     * project versions, or empty array when such project versions do not exist.
     * 
     * @throws WSException
     * <ul>
     *  <li>if the connection can't be established to the SQO-OSS's web services service</li>
     *  <li>if web services service throws an exception</li>
     * <ul>
     */
    public abstract WSProjectVersion[] getFirstProjectVersions(long[] projectsIds) throws WSException;

    /**
     * The method returns all information, that the SQO-OSS framework has
     * collected about the last versions of the projects.
     * 
     * @param projectsIds - the projects' identifiers
     * 
     * @return The <code>WSProjectVersion</code> array that describes the
     * project versions, or empty array when such project versions do not exist.
     * 
     * @throws WSException
     * <ul>
     *  <li>if the connection can't be established to the SQO-OSS's web services service</li>
     *  <li>if web services service throws an exception</li>
     * <ul>
     */
    public abstract WSProjectVersion[] getLastProjectVersions(long[] projectsIds) throws WSException;

    // TODO: JavaDoc
    public abstract WSProjectVersion getPreviousVersionById(long versionId)
            throws WSException;

    // TODO: JavaDoc
    public abstract WSProjectVersion getNextVersionById(long versionId)
            throws WSException;

    /**
     * The method returns all information, that the SQO-OSS framework has
     * collected about the specified projects.
     * 
     * @param projectsIds - the projects' identifiers
     * 
     * @return The <code>WSStoredProject</code> array that describes the
     * projects, or empty array when such projects do not exist.
     * 
     * @throws WSException
     * <ul>
     *  <li>if the connection can't be established to the SQO-OSS's web services service</li>
     *  <li>if web services service throws an exception</li>
     * <ul>
     */
    public abstract WSStoredProject[] getProjectsByIds(long[] projectId) throws WSException;

    /**
     * Returns the total number of versions for the project with the given Id
     * that are stored in the SQO-OSS framework.
     *
     * @param projectId - the project's identifier
     *
     * @return The total number of version for that project.
     * 
     * @throws WSException
     * <ul>
     *  <li>if a connection with the SQO-OSS's web services service can
     *    not be established</li>
     *  <li>if web services service throws an exception</li>
     * <ul>
     */
    public abstract long getVersionsCount(long projectId) throws WSException;

    /**
     * This method returns file statistic per given project version.
     *
     * @param projectVerionsIds - the list of project versions' identifiers
     *
     * @return The <code>WSVersionStats</code> array of objects which describe
     *   the file statistics for the given project versions,
     *   or <code>null</code> when none of the given project versions exist.
     *
     * @throws WSException
     * <ul>
     *  <li>if a connection with the SQO-OSS's web services service can
     *    not be established</li>
     *  <li>if web services service throws an exception</li>
     * <ul>
     */
    public abstract WSVersionStats[] getVersionsStatistics(
            long[] projectVersionsIds) throws WSException;

    /**
     * The method returns an array of all file groups that exists in the specified
     * project version.
     *
     * @param projectVersionId - the project's version identifier
     *
     * @return The array of project's file groups in that project version, or a
     *   <code>null</code> array when none are found.
     *   
     * @throws WSException
     * <ul>
     *  <li>if a connection with the SQO-OSS's web services service can
     *    not be established</li>
     *  <li>if web services service throws an exception</li>
     * <ul>
     */
    public abstract WSFileGroup[] getFileGroupsByProjectVersionId(
            long projectVersionId) throws WSException;
    
    /**
     * This method returns the root directory of the specified project's
     * source tree.
     *
     * @param projectId - the project's identifier
     *
     * @return The root directory's object, or <code>null</code> if not found.
     *
     * @throws WSException
     * <ul>
     *  <li>if a connection with the SQO-OSS's web services service can
     *    not be established</li>
     *  <li>if web services service throws an exception</li>
     * <ul>
     */
    public abstract WSDirectory getRootDirectory(
            long projectId) throws WSException;

    /**
     * This method returns an array of all files located in the selected
     * directory, that exists in the specified project version.
     *
     * @param projectVersionId - the project's version identifier
     * @param directoryId - the directory identifier
     *
     * @return The array of project's files in that directory and that project
     * version, or a <code>null</code> array when none are found.
     *
     * @throws WSException
     * <ul>
     *  <li>if a connection with the SQO-OSS's web services service can
     *    not be established</li>
     *  <li>if web services service throws an exception</li>
     * <ul>
     */
    public abstract WSProjectFile[] getFilesInDirectory(
            long projectVersionId,
            long directoryId) throws WSException;

    /**
     * This method returns an array of <code>WSFileModication<code> objects
     * that represent all modification that were performed on the project file
     * with the given Id.
     *
     * @param projectVersionId - the project version identifier
     * @param projectFileId - the project file identifier
     *
     * @return The file's modification history as array,
     *   or a <code>null</code> array when this file can not be found.
     *
     * @throws WSException
     * <ul>
     *  <li>if a connection with the SQO-OSS's web services service can
     *    not be established</li>
     *  <li>if web services service throws an exception</li>
     * <ul>
     */
    public abstract WSFileModification[] getFileModifications(
            long projectVersionId,
            long projectFileId) throws WSException;

    //========================================================================
    // TIMELINE RELATED PROJECT METHODS
    //========================================================================

    /**
     * This method will return the list of project version, associated to
     * project related events which had happened during the given time period
     * (<i>specified using the <code>tsmFrom<code> and <code>tsmTill</code>
     * timestamps</i>).
     * 
     * @param projectId the project's identifier
     * @param tsmFrom the timestamp of the period begin
     * @param tsmTill the timestamp of the period end
     * 
     * @throws WSException
     * <ul>
     *  <li>if a connection link with the SQO-OSS's Web-Services service can
     *    not be established at this time</li>
     *  <li>if the SQO-OSS's Web-Services service itself throw an exception</li>
     * <ul>
     */
    public abstract WSProjectVersion[] getSCMTimeline(
            long projectId, long tsmFrom, long tsmTill) throws WSException;

    /**
     * This method will return the list of email messages, associated to
     * project related events which had happened during the given time period
     * (<i>specified using the <code>tsmFrom<code> and <code>tsmTill</code>
     * timestamps</i>).
     * 
     * @param projectId the project's identifier
     * @param tsmFrom the timestamp of the period begin
     * @param tsmTill the timestamp of the period end
     * 
     * @return The array of <code>WSMailMessage</code> objects that describe
     * all located email messages, or <code>null</code> when no email related
     * events exist in the given time period.
     * 
     * @throws WSException
     * <ul>
     *  <li>if a connection link with the SQO-OSS's Web-Services service can
     *    not be established at this time</li>
     *  <li>if the SQO-OSS's Web-Services service itself throw an exception</li>
     * <ul>
     */
    public abstract WSMailMessage[] getMailTimeline(
            long projectId, long tsmFrom, long tsmTill) throws WSException;
}

//vi: ai nosi sw=4 ts=4 expandtab
