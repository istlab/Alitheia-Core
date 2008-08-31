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
package eu.sqooss.metrics.quality.bean;

import java.util.Arrays;
import java.util.List;

/**
 * This class represents a composed criterion of the Quality Model.<br>
 * It consists of a list of <code>subCriteria</code> and of a list of
 * <code>profilesValues</code>, containing a value for each profile. It also
 * provides a preference method to check an assigned profile against a profile
 * value according to its scale.
 * 
 * @see Criterion
 * 
 * @author <a href="mailto:sskalist@gmail.com">sskalist &lt sskalist@gmail.com
 *         &gt</a>
 * 
 */
public class ComposedCriterion extends Criterion {

    /**
     * The list of sub-criteria from which this Criterion is composed of.
     */
    private List<Criterion> subCriteria;

    /**
     * A list of {@link SQOOSSProfiles} values. Each element represents the
     * value this ComposedCriterion should have in order to be assigned to the
     * profile from {@link SQOOSSProfiles#values()}.
     */
    private List<SQOOSSProfiles> profilesValues;

    /**
     * A simple Constructor which initializes the ComposedCriterion's fields.
     * 
     * @param name
     *            The <code>name</code> of the Criterion.
     * @param relativeImportance
     *            The <code>relativeImportance</code> of the Criterion.
     * @param subCriteria
     *            A list of <code>subCriteria</code> from which this Criterion
     *            is composed of.
     * @param profilesValues
     *            A list of values representing the values for each profile in
     *            {@link SQOOSSProfiles#values()}.
     * @param criterionScale
     *            The scale at which the criterion is compared against a
     *            profile.
     * 
     * 
     * @see #initialize(List, List)
     * @see Criterion#Criterion(String, double, CriterionScale)
     */
    public ComposedCriterion(String name, double relativeImportance,
            List<Criterion> subCriteria, List<SQOOSSProfiles> profilesValues,
            CriterionScale criterionScale) {
        super(name, relativeImportance, criterionScale);

        initialize(subCriteria, profilesValues);
    }

    /**
     * A simple Constructor which initializes the ComposedCriterion's fields
     * using the default scale. <br>
     * Default scale is {@link CriterionScale#MoreIsBetter}.
     * 
     * @param name
     *            The <code>name</code> of the Criterion.
     * @param relativeImportance
     *            The <code>relativeImportance</code> of the Criterion.
     * @param subCriteria
     *            A list of <code>subCriteria</code> from which this Criterion
     *            is composed of.
     * @param profilesValues
     *            A list of values representing the values for each profile in
     *            {@link SQOOSSProfiles#values()}.
     * 
     * @see #initialize(List, List)
     * @see Criterion#Criterion(String, double, CriterionScale)
     */
    public ComposedCriterion(String name, double relativeImportance,
            List<Criterion> subCriteria, List<SQOOSSProfiles> profilesValues) {
        super(name, relativeImportance, CriterionScale.MoreIsBetter);

        initialize(subCriteria, profilesValues);
    }

    /**
     * A simple Constructor which initializes the ComposedCriterion's fields
     * using the default scale and <code>profilesValues</code> list. <br>
     * Default scale is {@link CriterionScale#MoreIsBetter} while default
     * <code>profilesValues</code> list is considered the list returned by
     * {@link SQOOSSProfiles#values()}.
     * 
     * @param name
     *            The <code>name</code> of the Criterion.
     * @param relativeImportance
     *            The <code>relativeImportance</code> of the Criterion.
     * @param subCriteria
     *            A list of <code>subCriteria</code> from which this Criterion
     *            is composed of.
     * 
     * @see #initialize(List, List)
     * @see Criterion#Criterion(String, double, CriterionScale)
     */
    public ComposedCriterion(String name, double relativeImportance,
            List<Criterion> subCriteria) {
        super(name, relativeImportance, CriterionScale.MoreIsBetter);

        List<SQOOSSProfiles> profilesValues = Arrays.asList(SQOOSSProfiles
                .values());
        initialize(subCriteria, profilesValues);
    }

    /**
     * Initializes the ComposedCriterion with the parameters given.<br>
     * It also checks for the validity of these parameters.
     * 
     * @param subCriteria
     *            A list of <code>subCriteria</code> from which this Criterion
     *            is composed of.
     * @param profilesValues
     *            A list of values representing the values for each profile in
     *            {@link SQOOSSProfiles#values()}.
     */
    private void initialize(List<Criterion> subCriteria,
            List<SQOOSSProfiles> profilesValues) {
        this.subCriteria = subCriteria;
        // TODO: Change the type of the Exception
        if (!isSubCreteriaValid())
            throw new RuntimeException("The sum of relative importance of '"
                    + this.getName() + "' is not equal to 1.0!");

        this.profilesValues = profilesValues;

        if (!isProfilesValuesValid())
            throw new RuntimeException("Invalid Number of Profile Values");

        if (!isRelativeImportanceValid())
            throw new RuntimeException("Invalid Relative Importance Value");

    }

    /**
     * Checks whether the {@link #profilesValues} are valid.
     * 
     * @return If they are valid, true; else false.
     */
    private boolean isProfilesValuesValid() {
        if (profilesValues.size() != SQOOSSProfiles.getNumberOfProfiles())
            return false;
        // TODO check values order according to criterionScale.
        return true;
    }

    /**
     * Always true, since it is composite.
     * 
     * @return true.
     * @see Criterion#isComposite()
     */
    @Override
    public boolean isComposite() {
        return true;
    }

    /**
     * Compares the profile to be <code>assigned</code>against a
     * <code>versusProfile</code> according to the object's criterionScale.
     * 
     * @param assigned
     *            The profile of the criterion.
     * @param versusProfile
     *            The profileValue of the profile.
     * @return if <code>assigned</code> is preferred, returns 1;<br>
     *         if <code>versusProfile</code> is preferred, returns -1;<br>
     *         if there is no preference between them, returns 0.
     * @see Criterion#compare(SQOOSSProfiles, SQOOSSProfiles, CriterionScale)
     */
    protected int preference(SQOOSSProfiles assigned,
            SQOOSSProfiles versusProfile) {

        return compare(assigned, versusProfile, this.getCriterionScale());
    }

    /**
     * Checks whether the {@link #subCriteria} list is valid. <br>
     * It sums the <code>relativeImportance</code> of each element and checks
     * if it equal to 0.0 or 1.0.
     * 
     * @return If it is valid, returns true; else false.
     */
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
     * Gets the {@link #subCriteria} of the ComposedCriterion.
     * 
     * @return the {@link #subCriteria}.
     */
    public List<Criterion> getSubCriteria() {
        return subCriteria;
    }

    /**
     * Sets the {@link #subCriteria} of the ComposedCriterion.
     * 
     * @param subCriteria
     *            the {@link #subCriteria} to set.
     */
    public void setSubCriteria(List<Criterion> subCriteria) {
        this.subCriteria = subCriteria;
    }

    /**
     * Gets the {@link #profilesValues} list of the ComposedCriterion.
     * 
     * @return the {@link #profilesValues} list.
     */
    public List<SQOOSSProfiles> getProfilesValues() {
        return profilesValues;
    }

    /**
     * Gets the <code>value</code> of the {@link #profilesValues} list at the
     * specified <code>index</code>.
     * 
     * @param atIndex
     *            list index.
     * @return the <code>profileValue</code>.
     */
    public SQOOSSProfiles getProfilesValue(int atIndex) {
        return profilesValues.get(atIndex);
    }

    /**
     * Sets the {@link #profilesValues} list of the ComposedCriterion.
     * 
     * @param profilesValues
     *            the {@link #profilesValues} list to set.
     */
    public void setProfilesValues(List<SQOOSSProfiles> profilesValues) {
        this.profilesValues = profilesValues;
    }

}
