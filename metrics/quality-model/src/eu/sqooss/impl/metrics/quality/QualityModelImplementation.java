/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007-2008 by the SQO-OSS consortium members <info@sqo-oss.eu>
 * Copyright 2007-2008 by Stefanos Skalistis <sskalistis@gmail.com>
 * 											 <sskalist@gmail.com>
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

package eu.sqooss.impl.metrics.quality;

import java.util.List;

import org.osgi.framework.BundleContext;

import eu.sqooss.metrics.quality.QualityModel;
import eu.sqooss.service.abstractmetric.AbstractMetric;
import eu.sqooss.service.abstractmetric.ResultEntry;
import eu.sqooss.service.db.Metric;
import eu.sqooss.service.db.MetricType;
import eu.sqooss.service.db.ProjectVersion;


public class QualityModelImplementation extends AbstractMetric implements QualityModel {

	public QualityModelImplementation(BundleContext bc) {
        super(bc);     
    }
      
    public boolean install() { 	
    	boolean result = true;

    	super.addSupportedMetrics("Quality", "QUAL", MetricType.Type.SOURCE_CODE);
    	//TODO add ALL the metrics dependecies when they are ready!
    	result &= super.install();
    	return result;
    }

    public boolean remove() {
    	boolean result = super.remove();
    	return result;
    }

    public boolean update() {

        return remove() && install(); 
    }

	public List<ResultEntry> getResult(ProjectVersion a, Metric m) {
		// TODO Auto-generated method stub
		return null;
	}

	public void run(ProjectVersion v) {
		// TODO Auto-generated method stub
		
	}
}

// vi: ai nosi sw=4 ts=4 expandtab
