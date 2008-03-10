/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007-2008 by the SQO-OSS consortium members <info@sqo-oss.eu>
 * Copyright 2008 by Vassilios Karakoidas <bkarak@aueb.gr>
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

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import eu.sqooss.impl.service.CoreActivator;

/**
 *
 *
 * @author Vassilios Karakoidas (bkarak@aueb.gr)
 */
public class MailingList extends DAObject {
    private String listId;
    private StoredProject storedProject;

    public MailingList() {}

    public String getListId() {
        return listId;
    }

    public void setListId(String li) {
        this.listId = li;
    }

    public StoredProject getStoredProject() {
        return storedProject;
    }

    public void setStoredProject(StoredProject sp) {
        this.storedProject = sp;
    }

    public List<MailMessage> getMessages()
    {
        DBService dbs = CoreActivator.getDBService();

        String paramListId = "mlist_listid";
        String query = "Select mm " +
                       "from MailMessage mm " + 
                       "where mm.MailingList.mlist_listid=:" +
                       paramListId;

        Map<String,Object> parameters = new HashMap<String,Object>();
        parameters.put(paramListId, this.getListId());

        List<?> msgList = dbs.doHQL(query, parameters);
	    if ((msgList == null) || (msgList.size()==0)) {
	        return null;
    	}
	
        return (List<MailMessage>)msgList;
    }

    public static List<MailingList> getListsPerProject(StoredProject sp) throws DAOException {
        DBService dbs = CoreActivator.getDBService();
        List<MailingList> ml = new ArrayList<MailingList>();

        // TODO: query needs testing, and all of this maybe rewrite
        List mllist = dbs.doHQL("from MailingList where PROJECT_ID = " + sp.getId());
        int mllistLen = mllist.size();
        if (mllistLen == 0) {
            // TODO: Why throw? Why not just return an empty list here?
            throw new DAOException("MailingList", "No list found for project " + sp.getName());
        }
        for (int i = 0;i < mllistLen;i++) {
            ml.add((MailingList)mllist.get(i));
        }

        return ml;
    }
}
