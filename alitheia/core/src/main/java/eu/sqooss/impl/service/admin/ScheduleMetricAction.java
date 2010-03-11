/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2008 - 2010 - Organization for Free and Open Source Software,
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

package eu.sqooss.impl.service.admin;

import java.util.Map;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.service.abstractmetric.AlitheiaPlugin;
import eu.sqooss.service.admin.ActionParam;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.Plugin;
import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.metricactivator.MetricActivator;
import eu.sqooss.service.pa.PluginAdmin;
import eu.sqooss.service.pa.PluginInfo;

public class ScheduleMetricAction extends ActionBase {

	public ScheduleMetricAction() {
		registerParam(ActionParam.PROJECT_ID, true);
		registerParam(ActionParam.PLUGIN_ID, true);
	}
	
	@Override
	public String getActionDescr() {
		return "Schedule a plug-in run for all activation types on the" +
				" provided project";
	}

	@Override
	public String getActionName() {
		return "schedplug";
	}
	
	@Override
	public boolean execute(Map<ActionParam, Object> params) {
		DBService db = AlitheiaCore.getInstance().getDBService();
		PluginAdmin pa = AlitheiaCore.getInstance().getPluginAdmin();
		MetricActivator ma = AlitheiaCore.getInstance().getMetricActivator();
		
		StoredProject sp = StoredProject.getProjectByName(
        		(String)params.get(ActionParam.PROJECT_NAME));
        
		Plugin p = Plugin.loadDAObyId(Integer.parseInt(((String)params.get(ActionParam.PLUGIN_ID))), Plugin.class);
		
		PluginInfo pInfo = pa.getPluginInfo(p.getHashcode());
		if (pInfo != null) {
			AlitheiaPlugin ap = pa.getPlugin(pInfo);
			if (ap != null) {
				ma.syncMetric(ap, sp);
				return true;
			} else {
				return false;
			}
		}
		return false;
	}
}
