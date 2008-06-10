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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author sskalist
 * 
 */
public class ComposedCriterion extends Criterion {

	private List<Criterion> subCriteria;

	private List<SQOOSSProfiles> profilesValues;

	public ComposedCriterion(String name, double relativeImportance,
			List<Criterion> subCriteria, List<SQOOSSProfiles> profilesValues,
			CriterionScale criterionScale) {
		super(name, relativeImportance, criterionScale);

		initialize(name, subCriteria, relativeImportance, profilesValues);
	}

	public ComposedCriterion(String name, double relativeImportance,
			List<Criterion> subCriteria, List<SQOOSSProfiles> profilesValues) {
		super(name, relativeImportance, CriterionScale.MoreIsBetter);

		initialize(name, subCriteria, relativeImportance, profilesValues);
	}

	public ComposedCriterion(String name, double relativeImportance,
			List<Criterion> subCriteria) {
		super(name, relativeImportance, CriterionScale.MoreIsBetter);

		List<SQOOSSProfiles> profilesValues = Arrays.asList(SQOOSSProfiles
				.values());
		initialize(name, subCriteria, relativeImportance, profilesValues);
	}

	private void initialize(String name, List<Criterion> subCriteria,
			double relativeImportance, List<SQOOSSProfiles> profilesValues) {
		this.subCriteria = subCriteria;
		// TODO: Change the type of the Exception
		if (!isSubCreteriaValid())
			throw new RuntimeException("The sum of relative importance of '"
					+ name + "' is not equal to 1.0!");

		this.profilesValues = profilesValues;

		if (!isProfilesValuesValid())
			throw new RuntimeException("Invalid Number of Profile Values");

		this.setRelativeImportance(relativeImportance);
		if (!isRelativeImportanceValid())
			throw new RuntimeException("Invalid Relative Importance Value");

	}

	private boolean isProfilesValuesValid() {
		if (profilesValues.size() != SQOOSSProfiles.getNumberOfProfiles())
			return false;
		// TODO check values order according to criterionScale.
		return true;
	}

	@Override
	public boolean isComposite() {
		return true;
	}

	protected int preference(SQOOSSProfiles assigned,
			SQOOSSProfiles versusProfile) {

		return compare(assigned, versusProfile, this.getCriterionScale());
	}

	protected boolean isSubCreteriaValid() {
		double sum = 0.0d;

		for (Criterion criterion : subCriteria) {
			sum += criterion.getRelativeImportance();
		}
		if (sum != 1.0d && sum != 0.0d)
			return false;

		return true;
	}

	/**
	 * @return the subCriteria
	 */
	public List<Criterion> getSubCriteria() {
		return subCriteria;
	}

	/**
	 * @param subCriteria
	 *            the subCriteria to set
	 */
	public void setSubCriteria(List<Criterion> subCriteria) {
		this.subCriteria = subCriteria;
	}

	/**
	 * @return the profilesValues
	 */
	public List<SQOOSSProfiles> getProfilesValues() {
		return profilesValues;
	}

	public SQOOSSProfiles getProfilesValues(int atIndex) {
		return profilesValues.get(atIndex);
	}

	/**
	 * @param profilesValues
	 *            the profilesValues to set
	 */
	public void setProfilesValues(ArrayList<SQOOSSProfiles> profilesValues) {
		this.profilesValues = profilesValues;
	}

}
