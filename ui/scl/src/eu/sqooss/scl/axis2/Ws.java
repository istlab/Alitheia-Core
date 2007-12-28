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
package eu.sqooss.scl.axis2;

/*
 *  Ws java interface
 */

public interface Ws {


    /**
     * Auto generated method signature

     * @param param0

     */
    public eu.sqooss.scl.axis2.ws.EvaluatedProjectsListResponse evaluatedProjectsList(

            eu.sqooss.scl.axis2.ws.EvaluatedProjectsList param0)
    throws java.rmi.RemoteException

    ;



    /**
     * Auto generated method signature

     * @param param2

     */
    public eu.sqooss.scl.axis2.ws.RetrieveMetrics4SelectedProjectResponse retrieveMetrics4SelectedProject(

            eu.sqooss.scl.axis2.ws.RetrieveMetrics4SelectedProject param2)
    throws java.rmi.RemoteException

    ;



    /**
     * Auto generated method signature

     * @param param4

     */
    public eu.sqooss.scl.axis2.ws.RetrieveFileListResponse retrieveFileList(

            eu.sqooss.scl.axis2.ws.RetrieveFileList param4)
    throws java.rmi.RemoteException

    ;



    /**
     * Auto generated method signature

     * @param param6

     */
    public eu.sqooss.scl.axis2.ws.RetrieveSelectedMetricResponse retrieveSelectedMetric(

            eu.sqooss.scl.axis2.ws.RetrieveSelectedMetric param6)
    throws java.rmi.RemoteException

    ;



    /**
     * Auto generated method signature

     * @param param8

     */
    public eu.sqooss.scl.axis2.ws.RetrieveMetrics4SelectedFilesResponse retrieveMetrics4SelectedFiles(

            eu.sqooss.scl.axis2.ws.RetrieveMetrics4SelectedFiles param8)
    throws java.rmi.RemoteException

    ;




    //
}

//vi: ai nosi sw=4 ts=4 expandtab
