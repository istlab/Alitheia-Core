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

package eu.sqooss.scl;

/**
 * The interface has package visibility.
 */
interface WSConnectionConstants {
    
    public static final String METHOD_NAME_EVALUATED_PROJECTS_LIST              = "evaluatedProjectsList";

    public static final String METHOD_NAME_STORED_PROJECTS_LIST                 = "storedProjectsList";
    
    public static final String METHOD_NAME_RETRIEVE_METRICS_4_SELECTED_PROJECT  = "retrieveMetrics4SelectedProject";
    
    public static final String METHOD_NAME_RETRIEVE_SELECTED_METRIC             = "retrieveSelectedMetric";
    
    public static final String METHOD_NAME_RETRIEVE_METRICS_4_SELECTED_FILES    = "retrieveMetrics4SelectedFiles";
    
    public static final String METHOD_NAME_RETRIEVE_FILE_LIST                   = "retrieveFileList";
    
    public static final String METHOD_NAME_REQUEST_EVALUATION_4_PROJECT         = "requestEvaluation4Project";
    
    public static final String METHOD_NAME_DISPLAY_USER                         = "displayUser";
    
    public static final String METHOD_NAME_DELETE_USER                          = "deleteUser";
    
    public static final String METHOD_NAME_MODIFY_USER                          = "modifyUser";
    
    public static final String METHOD_NAME_SUBMIT_USER                          = "submitUser";
    
    public static final String METHOD_NAME_RETRIEVE_PROJECT_ID                  = "retrieveProjectId";
    
}

//vi: ai nosi sw=4 ts=4 expandtab
