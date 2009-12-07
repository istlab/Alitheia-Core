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

import java.util.ArrayList;
import java.util.List;

import eu.sqooss.service.db.ProjectVersion;
import eu.sqooss.service.db.Tag;

/**
 * This class wraps the <code>eu.sqooss.service.db.ProjectVersion</code> and
 * adds support for version tags.
 */
public class WSTaggedVersion extends WSProjectVersion {
    private String[] tags;

    public String[] getTags() {
        return tags;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }

    /**
     * This method creates a new <code>WSTaggedVersion</code> object, based on
     * the given <code>ProjectVersion</code> DAO object.
     * 
     * @param projectVersion the <code>ProjectVersion</code> DAO
     * 
     * @return The new <code>WSTaggedVersion</code> object.
     */
    public static WSTaggedVersion getInstance(ProjectVersion projectVersion) {
        if (projectVersion == null) return null;
        if (projectVersion.getTags() == null) return null;
        try {
            WSTaggedVersion result = new WSTaggedVersion();
            result.setId(projectVersion.getId());
            result.setCommitMsg(projectVersion.getCommitMsg());
            result.setCommitterId(projectVersion.getCommitter().getId());
            result.setProjectId(projectVersion.getProject().getId());
            result.setProperties(projectVersion.getProperties());
            result.setTimestamp(projectVersion.getTimestamp());
            result.setVersion(projectVersion.getRevisionId());

            // Retrieve all tags for the given project version
            String[] tags = new String[projectVersion.getTags().size()];
            int index = 0;
            for (Tag tag : projectVersion.getTags()) 
                tags[index++] = tag.getName();
            result.setTags(tags);

            return result;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * This method converts a list of <code>ProjectVersion</code> DAOs
     * into an array of <code>WSTaggedVersion</code> objects.
     * 
     * @param projectVersions the list of <code>ProjectVersion</code> DAOs
     * 
     * @return - an array of <code>WSTaggedVersion</code> objects, or
     * <code>null</code>, if the list is <code>null<code> or empty.
     */
    public static WSTaggedVersion[] asArray(List<ProjectVersion> versions) {
        if ((versions == null) || (versions.isEmpty()))
            return null;
        List<WSTaggedVersion> result = new ArrayList<WSTaggedVersion>();
        if (versions != null) {
            for (ProjectVersion version : versions)
                result.add(WSTaggedVersion.getInstance(version));
        }
        return result.toArray(new WSTaggedVersion[result.size()]);
    }
}

//vi: ai nosi sw=4 ts=4 expandtab
