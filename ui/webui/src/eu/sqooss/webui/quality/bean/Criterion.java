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
 * @author <a href="mailto:sskalist@gmail.com">sskalist &lt sskalist@gmail.com
 *         &gt</a>
 * 
 */
public abstract class Criterion {

    /**
     * The <code>name</code> of the Criterion.
     */
    private String name;

    /**
     * The relative importance of the specific criterion. <br>
     * The value must be between <code>0.0</code> and <code>1.0</code>.
     */
    private double relativeImportance;

    /**
     * The scale at which the value of {@link NumericCriterionElement} or
     * {@link ComposedCriterion} is compared to the profile values.
     */
    private CriterionScale criterionScale;

    /**
     * The optimistic assignment of the {@link QualityEvaluator} for this
     * Criterion.
     * 
     * @see QualityEvaluator#optimisticAssignment(ComposedCriterion)
     */
    private SQOOSSProfiles optimisticProfile;

    /**
     * The pessimistic assignment of the {@link QualityEvaluator} for this
     * Criterion.
     * 
     * @see QualityEvaluator#optimisticAssignment(ComposedCriterion)
     */
    private SQOOSSProfiles pessimisticProfile;

    /**
     * A simple constructor.
     * 
     * @param name
     *            The <code>name</code> of the Criterion.
     * @param relativeImportance
     *            The <code>relativeImportance</code> of the Criterion.
     * @param criterionScale
     *            The scale at which the criterion is compared against a
     *            profile.
     */
    Criterion(String name, double relativeImportance,
            CriterionScale criterionScale) {
        this.name = name;
        this.relativeImportance = relativeImportance;
        this.criterionScale = criterionScale;
        optimisticProfile = null;
        pessimisticProfile = null;
    }

    /**
     * Gets the {@link #optimisticProfile}. <br>
     * If it hasn't been evaluated yet, it is evaluated and then returns the
     * result.
     * 
     * @return The optimistic assignment for the specific criterion
     */
    public SQOOSSProfiles getOptimisticProfile() {
        if (optimisticProfile == null) {
            // optimisticProfile = QualityEvaluator.optimisticAssignment(this);
        }
        return optimisticProfile;
    }

    /**
     * Gets the{@link #pessimisticProfile}. <br>
     * If it hasn't been evaluated yet, it is evaluated and then returns the
     * result.
     * 
     * @return The pessimistic assignment for the specific criterion
     */
    public SQOOSSProfiles getPessimisticProfile() {
        if (pessimisticProfile == null) {
            // pessimisticProfile =
            // QualityEvaluator.pessimisticAssignment(this);
        }
        return pessimisticProfile;
    }

    /**
     * Compares a metric value against a profile value according to the scale
     * provided.
     * 
     * @param metricValue
     *            The value of the metric.
     * @param profileValue
     *            The value of the profile.
     * @param criterionScale
     *            The scale according to which the values should be compared.
     * @return if <code>metricValue</code> is preferred, returns 1;<br>
     *         if <code>profileValue</code> is preferred, returns -1;<br>
     *         if there is no preference between them, returns 0.
     */
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

            return Double.compare(metricValue,profileValue) == 0 ? 1 : -1;

        // In case something goes wrong..ignore it.
        return 0;
    }

    /**
     * Compares a <code>criterionProfile</code>against a
     * <code>profileValue</code>according to the scale provided.
     * 
     * @param criterionProfile
     *            The profile of the criterion.
     * @param profileValue
     *            The profileValue of the profile.
     * @param criterionScale
     *            The scale according to which the values should be compared.
     * @return if <code>criterionProfile</code> is preferred, returns 1;<br>
     *         if <code>profileValue</code> is preferred, returns -1;<br>
     *         if there is no preference between them, returns 0.
     */
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

    /**
     * Checks if the <code>Criterion</code> is composite or not.
     * 
     * @return If the <code>Criterion</code> is composite, returns true; else false.
     */
    public abstract boolean isComposite();

    /**
     * Check whether {@link #relativeImportance} is between valid bounds.
     * 
     * @return If is valid, returns true; else false.
     */
    protected boolean isRelativeImportanceValid() {
        if (relativeImportance < 0.0d || relativeImportance > 1.0d)
            return false;
        return true;
    }

    /**
     * Gets the {@link #relativeImportance} of the Criterion.
     * 
     * @return the {@link #relativeImportance}
     */
    public double getRelativeImportance() {
        return relativeImportance;
    }

    /**
     * Sets the {@link #relativeImportance} of the Criterion.
     * 
     * @param relativeImportance
     *            the {@link #relativeImportance} to set.
     */
    public void setRelativeImportance(double relativeImportance) {
        this.relativeImportance = relativeImportance;
    }

    /**
     * Gets the {@link #criterionScale} of the Criterion.
     * 
     * @return the {@link #criterionScale}
     */
    public CriterionScale getCriterionScale() {
        return criterionScale;
    }

    /**
     * Sets the {@link #criterionScale} of the Criterion.
     * 
     * @param criterionScale
     *            the {@link #criterionScale} to set.
     */
    public void setCriterionScale(CriterionScale criterionScale) {
        this.criterionScale = criterionScale;
    }

    /**
     * Gets the {@link #name} of the Criterion.
     * 
     * @return the {@link #name}
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the {@link #name} of the Criterion.
     * 
     * @param name
     *            the {@link #name} to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name.toString();
    }

}