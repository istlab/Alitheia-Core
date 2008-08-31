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

import java.util.List;

/**
 * This class represents a criterion element of the Quality Model which has a
 * numeric value.<br>
 * It consists of a numeric value ({@link #metricValue}) and of a list of
 * <code>profilesValues</code>, containing a numeric value for each profile.
 * It also provides a preference method to check a metric value against a
 * profile value according to its scale.
 * 
 * @see Criterion
 * 
 * @author <a href="mailto:sskalist@gmail.com">sskalist &lt sskalist@gmail.com
 *         &gt</a>
 * 
 */
public class NumericCriterionElement extends Criterion {

    /**
     * The value from the metric.
     */
    private double metricValue;

    /**
     * A list of values containing a value for each profile in {@link SQOOSSProfiles#values()}.
     */
    private List<Double> profilesValues;

    /**
     * A simple Constructor which initializes the NumericCriterionElement's
     * fields.
     * 
     * @param name
     *            The <code>name</code> of the Criterion.
     * @param relativeImportance
     *            The <code>relativeImportance</code> of the Criterion.
     * @param metricValue
     *            The numeric value of this Criterion element.
     * @param criterionScale
     *            The scale at which the criterion is compared against a
     *            profile.
     * @param profilesValues
     *            A list of values representing the values for each profile in
     *            {@link SQOOSSProfiles#values()}.
     * 
     * 
     * @see #initialize(double, List)
     * @see Criterion#Criterion(String, double, CriterionScale)
     */
    public NumericCriterionElement(String name, double relativeImportance,
            double metricValue, CriterionScale criterionScale,
            List<Double> profilesValues) {
        super(name, relativeImportance, criterionScale);
        initialize(metricValue, profilesValues);
    }

    /**
     * A simple Constructor which initializes the NumericCriterionElement's
     * fields using the default metricValue according to the scale. <br>
     * Default metricValues:<br>
     * <br>
     * <table border="1">
     * <tr>
     * <th> CriterionScale </th>
     * <th> MetricValue </th>
     * </tr>
     * <tr>
     * <td> MoreIsBetter </td>
     * <td> - Double.MAX_VALUE </td>
     * </tr>
     * <tr>
     * <td> LessIsBetter </td>
     * <td> Double.MAX_VALUE </td>
     * </tr>
     * <tr>
     * <td> ValueIsBetter </td>
     * <td> Double.NaN </td>
     * </tr>
     * </table>
     * 
     * @param name
     *            The <code>name</code> of the Criterion.
     * @param relativeImportance
     *            The <code>relativeImportance</code> of the Criterion.
     * @param criterionScale
     *            The scale at which the criterion is compared against a
     *            profile.
     * @param profilesValues
     *            A list of values representing the values for each profile in
     *            {@link SQOOSSProfiles#values()}.
     * 
     * 
     * @see #initialize(double, List)
     * @see Criterion#Criterion(String, double, CriterionScale)
     */
    public NumericCriterionElement(String name, double relativeImportance,
            CriterionScale criterionScale, List<Double> profilesValues) {
        super(name, relativeImportance, criterionScale);

        double metricValue;
        switch (criterionScale) {
        case MoreIsBetter:
            metricValue = -Double.MAX_VALUE;
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
    
    /**
     * Initializes the NumericCriterionElement with the parameters given.<br>
     * It also checks for the validity of these parameters.
     * 
     * @param metricValue
     *           the metricValue.
     * @param profilesValues
     *            A list of values representing the values for each profile in
     *            {@link SQOOSSProfiles#values()}.
     */
    private void initialize(double metricValue, List<Double> profilesValues) {
        this.metricValue = metricValue;
        if (profilesValues.size() != SQOOSSProfiles.getNumberOfProfiles())
            throw new RuntimeException("Invalid Number of Profile Values");
        // TODO check scale within profile values!
        this.profilesValues = profilesValues;
        
        if (!isRelativeImportanceValid())
            throw new RuntimeException("Invalid Relative Importance Value");
    }

    /**
     * Compares the {@link #metricValue} against a
     * <code>profile</code> according to the object's criterionScale.
     * 
     *
     * @param profile
     *            The profileValue of the profile.
     * @return if <code>assigned</code> is preferred, returns 1;<br>
     *         if <code>versusProfile</code> is preferred, returns -1;<br>
     *         if there is no preference between them, returns 0.
     * @see Criterion#compare(SQOOSSProfiles, SQOOSSProfiles, CriterionScale)
     */
    public int preference(SQOOSSProfiles profile) {
        return compare(metricValue, profilesValues.get(profile.ordinal()), this
                .getCriterionScale());
    }

    /**
     * Always false, since it is an element.
     * 
     * @return false.
     * @see Criterion#isComposite()
     */
    @Override
    public boolean isComposite() {
        return false;
    }

    /**
     * Gets the {@link #metricValue} of this numeric element.
     * 
     * @return the {@link #metricValue}.
     */
    public double getMetricValue() {
        return metricValue;
    }

    /**
     * Sets the {@link #metricValue} of this numeric element.
     * 
     * @param metricValue
     *            the {@link #metricValue} to set.
     */
    public void setMetricValue(double metricValue) {
        this.metricValue = metricValue;
    }

    /**
     * Gets the {@link #profilesValues} list of this numeric element.
     * 
     * @return the {@link #profilesValues}.
     */
    public List<Double> getProfilesValues() {
        return this.profilesValues;
    }
}
