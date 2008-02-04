/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007 by the SQO-OSS consortium members <info@sqo-oss.eu>
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

/**
 * Ws.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.1 Nov 13, 2006 (07:31:44 LKT)
 */
package eu.sqooss.ws.client;

/*
 *  Ws java interface
 */

public interface Ws {


    /**
     * Auto generated method signature

     * @param param0

     */
    public eu.sqooss.ws.client.ws.DisplayUserResponse displayUser(

            eu.sqooss.ws.client.ws.DisplayUser param0)
    throws java.rmi.RemoteException

    ;



    /**
     * Auto generated method signature

     * @param param2

     */
    public eu.sqooss.ws.client.ws.EvaluatedProjectsListResponse evaluatedProjectsList(

            eu.sqooss.ws.client.ws.EvaluatedProjectsList param2)
    throws java.rmi.RemoteException

    ;



    /**
     * Auto generated method signature

     * @param param4

     */
    public eu.sqooss.ws.client.ws.RetrieveMetrics4SelectedProjectResponse retrieveMetrics4SelectedProject(

            eu.sqooss.ws.client.ws.RetrieveMetrics4SelectedProject param4)
    throws java.rmi.RemoteException

    ;



    /**
     * Auto generated method signature

     * @param param6

     */
    public eu.sqooss.ws.client.ws.SubmitUserResponse submitUser(

            eu.sqooss.ws.client.ws.SubmitUser param6)
    throws java.rmi.RemoteException

    ;



    /**
     * Auto generated method signature

     * @param param8

     */
    public void modifyUser(

            eu.sqooss.ws.client.ws.ModifyUser param8)
    throws java.rmi.RemoteException

    ;



    /**
     * Auto generated method signature

     * @param param10

     */
    public eu.sqooss.ws.client.ws.ValidateAccountResponse validateAccount(

            eu.sqooss.ws.client.ws.ValidateAccount param10)
    throws java.rmi.RemoteException

    ;



    /**
     * Auto generated method signature

     * @param param12

     */
    public eu.sqooss.ws.client.ws.RequestEvaluation4ProjectResponse requestEvaluation4Project(

            eu.sqooss.ws.client.ws.RequestEvaluation4Project param12)
    throws java.rmi.RemoteException

    ;



    /**
     * Auto generated method signature

     * @param param14

     */
    public void deleteUser(

            eu.sqooss.ws.client.ws.DeleteUser param14)
    throws java.rmi.RemoteException

    ;



    /**
     * Auto generated method signature

     * @param param16

     */
    public eu.sqooss.ws.client.ws.RetrieveProjectIdResponse retrieveProjectId(

            eu.sqooss.ws.client.ws.RetrieveProjectId param16)
    throws java.rmi.RemoteException

    ;



    /**
     * Auto generated method signature

     * @param param18

     */
    public eu.sqooss.ws.client.ws.RetrieveFileListResponse retrieveFileList(

            eu.sqooss.ws.client.ws.RetrieveFileList param18)
    throws java.rmi.RemoteException

    ;



    /**
     * Auto generated method signature

     * @param param20

     */
    public eu.sqooss.ws.client.ws.RetrieveSelectedMetricResponse retrieveSelectedMetric(

            eu.sqooss.ws.client.ws.RetrieveSelectedMetric param20)
    throws java.rmi.RemoteException

    ;



    /**
     * Auto generated method signature

     * @param param22

     */
    public eu.sqooss.ws.client.ws.RetrieveMetrics4SelectedFilesResponse retrieveMetrics4SelectedFiles(

            eu.sqooss.ws.client.ws.RetrieveMetrics4SelectedFiles param22)
    throws java.rmi.RemoteException

    ;




    //
}

//vi: ai nosi sw=4 ts=4 expandtab
