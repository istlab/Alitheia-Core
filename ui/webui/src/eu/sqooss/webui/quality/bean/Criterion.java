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

/**
 * The Criterion class represents a criterion in our model. This is the
 * definition of the super class that contains the basic characteristics of all
 * Criteria in our model and also provides some comparing functionality between
 * values or profiles according to a scale.
 * 
 * @author sskalist
 * 
 */
public abstract class Criterion {

	private String name;

	private double relativeImportance;

	private CriterionScale criterionScale;

	private SQOOSSProfiles optimisticProfile;
	
	private SQOOSSProfiles pessimisticProfile;
	
	/**
	 * @param name
	 * @param relativeImportance
	 */
	Criterion(String name, double relativeImportance, CriterionScale criterionScale) {
		this.name = name;
		this.relativeImportance = relativeImportance;
		this.criterionScale = criterionScale;
		optimisticProfile = null;
		pessimisticProfile = null;
	}

	public SQOOSSProfiles getOptimisticProfile()
	{
		if(optimisticProfile == null){
		//	optimisticProfile = QualityEvaluator.optimisticAssignment(this);
		}
		return optimisticProfile;
	}
	
	public SQOOSSProfiles getPessimisticProfile()
	{
		if(pessimisticProfile == null){
		//	pessimisticProfile = QualityEvaluator.pessimisticAssignment(this);
		}
		return pessimisticProfile;
	}
	
	protected static int compare(double metricValue, double profileValue,
			CriterionScale criterionScale) {
		// Check the possibility that metricValue belongs to GEqauls set
		if (!criterionScale.equals(CriterionScale.ValueIsBetter)
				&& metricValue == profileValue)
			return 0;

		if (criterionScale.equals(CriterionScale.LessIsBetter))

			return (int) Math.signum(profileValue - metricValue);

		else if (criterionScale.equals(CriterionScale.MoreIsBetter))

			return (int) Math.signum(metricValue - profileValue);

		else if (criterionScale.equals(CriterionScale.ValueIsBetter))

			return metricValue == profileValue ? 1 : -1;

		// In case something goes wrong..ignore it.
		return 0;
	}

	protected static int compare(SQOOSSProfiles criterionProfile,
			SQOOSSProfiles profileValue, CriterionScale criterionScale) {
		// Default case scenario... The plus one is for equallity to be achived
		if (criterionScale.equals(CriterionScale.MoreIsBetter))
			return criterionProfile.compareTo(profileValue) + 1;
		else if (criterionScale.equals(CriterionScale.LessIsBetter))
			return profileValue.compareTo(criterionProfile) + 1;
		else if (criterionScale.equals(CriterionScale.ValueIsBetter))
			return criterionProfile.equals(profileValue) ? 1 : -1;

		// In case something goes wrong..ignore it.
		return 0;
	}

	public abstract boolean isComposite();

	protected boolean isRelativeImportanceValid() {
		if (relativeImportance < 0.0d || relativeImportance > 1.0d)
			return false;
		return true;
	}

	/**
	 * @return the relativeImportance
	 */
	public double getRelativeImportance() {
		return relativeImportance;
	}

	/**
	 * @param relativeImportance
	 *            the relativeImportance to set
	 */
	public void setRelativeImportance(double relativeImportance) {
		this.relativeImportance = relativeImportance;
	}

	/**
	 * @return the criterionScale
	 */
	public CriterionScale getCriterionScale() {
		return criterionScale;
	}

	/**
	 * @param criterionScale
	 *            the criterionScale to set
	 */
	public void setCriterionScale(CriterionScale criterionScale) {
		this.criterionScale = criterionScale;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return name.toString();
	}

}