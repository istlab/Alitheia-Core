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
package eu.sqooss.webui.quality.bean;

import java.util.List;

/**
 * @author sskalist
 * 
 */
public class NumericCriterionElement extends Criterion {

	private double metricValue;

	private List<Double> profilesValues;

	/**
	 * @param name
	 * @param relativeImportance
	 * @param metricValue
	 * @param criterionScale
	 * @param profilesValues
	 */
	public NumericCriterionElement(String name, double relativeImportance,
			double metricValue, CriterionScale criterionScale,
			List<Double> profilesValues) {
		super(name, relativeImportance,criterionScale);
		initialize(metricValue, profilesValues);
	}

	public NumericCriterionElement(String name, double relativeImportance,
			CriterionScale criterionScale,
			List<Double> profilesValues){
		super(name, relativeImportance,criterionScale);
		
		double metricValue;
		switch (criterionScale) {
		case MoreIsBetter:
			metricValue = - Double.MAX_VALUE;
			break;
		case LessIsBetter:
			metricValue = Double.MAX_VALUE;
			break;
		case ValueIsBetter:
			metricValue = Double.NaN;
			break;
		default:
			metricValue = Double.NaN;
			break;
		}
		
		initialize(metricValue, profilesValues);
	}
	private void initialize(double metricValue, List<Double> profilesValues){
		this.metricValue = metricValue;
		if (profilesValues.size() != SQOOSSProfiles.getNumberOfProfiles())
			throw new RuntimeException("Invalid Number of Profile Values");
		// TODO check scale within profile values!
		this.profilesValues = profilesValues;
	}
	public int preference(SQOOSSProfiles profile) {
		return compare(metricValue, profilesValues.get(profile.ordinal()),
				this.getCriterionScale());
	}

	
	@Override
	public boolean isComposite() {
		return false;
	}

	/**
	 * @return the metricValue
	 */
	public double getMetricValue() {
		return metricValue;
	}

	/**
	 * @param metricValue the metricValue to set
	 */
	public void setMetricValue(double metricValue) {
		this.metricValue = metricValue;
	}

	public List<Double>getProfilesValues(){
		return this.profilesValues;
	}
}
