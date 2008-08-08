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
import java.util.List;

/**
 * The Quality Evaluator is used in order to evaluate each {@link Criterion} in
 * the QualityModel.<br>
 * It provides both a pessimistic and an optimistic assignment for each
 * Criterion in the model using a concordance and a non-discordance test.<br> (
 * <strong>Note:</strong> The sum of {@link #concordanceThreshold} and
 * {@link #discordanceThreshold} should be less than <code>1.0</code>).
 * 
 * @author <a href="mailto:sskalist@gmail.com">sskalist &lt sskalist@gmail.com
 *         &gt</a>
 */
class QualityEvaluator {

    /**
     * The concordance threshold (default = 0.7).<br>
     * Used in {@link #concordanceTestElement(double, double)},{@link #concordanceTestProfile(double, double)}.
     */
    private static double concordanceThreshold;
    /**
     * The discordance threshold (default = 0.28)<br>
     * Used in {@link #discordanceTest(double)}.
     */
    private static double discordanceThreshold;

    /**
     * The profiles at which the evaluation method should be based upon.
     */
    private static SQOOSSProfiles profiles[];

    static {
        profiles = SQOOSSProfiles.values();
        concordanceThreshold = 0.7d;
        discordanceThreshold = 0.28d;
    }

    /**
     * Makes the discorance test.
     * 
     * @param relativeMinusWeightsSum
     *            the sum of all the sub-criterias' relative importance that
     *            belong to the G<sup>-</sup> list.
     * @return true if relativeMinusWeightsSum > {@link #discordanceThreshold};
     *         else false.
     */
    private static boolean discordanceTest(double relativeMinusWeightsSum) {
        return relativeMinusWeightsSum > discordanceThreshold;
    }

    /**
     * Makes a concordance test comparing an element against a profile.
     * 
     * @param relativeMinusWeightsSum
     *            the sum of all the sub-criterias' relative importance that
     *            belong to the G<sup>-</sup> list.
     * @param relativePlusWeightsSum
     *            the sum of all the sub-criterias' relative importance that
     *            belong to the G<sup>+</sup> list.
     * @return if the test succeeds, returns true; else false.
     */
    private static boolean concordanceTestElement(
            double relativeMinusWeightsSum, double relativePlusWeightsSum) {
        double relativeBothWeightsSum = relativeMinusWeightsSum
                + relativePlusWeightsSum;

        return relativeBothWeightsSum >= concordanceThreshold
                && relativePlusWeightsSum >= relativeMinusWeightsSum;
    }

    /**
     * Makes a concordance test comparing a profile against an element.
     * 
     * @param relativeMinusWeightsSum
     *            the sum of all the sub-profiles' relative importance that
     *            belong to the G<sup>-</sup> list.
     * @param relativePlusWeightsSum
     *            the sum of all the sub-profiles' relative importance that
     *            belong to the G<sup>+</sup> list.
     * @return if the test succeeds, returns true; else false.
     */
    private static boolean concordanceTestProfile(
            double relativeMinusWeightsSum, double relativePlusWeightsSum) {
        return relativePlusWeightsSum > relativeMinusWeightsSum
                || concordanceTestElement(relativeMinusWeightsSum,
                        relativePlusWeightsSum);
    }

    /**
     * Assigns the ComposedCriterion to a profile in a pessimistic way.<br>
     * It searches the {@link SQOOSSProfiles} from end to start until it finds a
     * profile that is definitely worst than the <code>criterion</code>.
     * 
     * @param criterion
     *            the {@link ComposedCriterion} to be assigned to a profile.
     * @return the profile that is definitely worse than the
     *         <code>criterion</code>.If none was found returns null.<br>(<strong>Note:</strong>
     *         Using the default profiles is never gets to return null, since
     *         the worst profile has such values that any criterion will be
     *         always preferred)
     */
    static SQOOSSProfiles pessimisticAssignment(ComposedCriterion criterion) {
        // Iteratively check all profiles from end to start
        for (int i = profiles.length - 1; i >= 0; i--) {
            // The GMINUS list.
            ArrayList<Criterion> GMinus = new ArrayList<Criterion>();
            // The GPLUS list.
            ArrayList<Criterion> GPlus = new ArrayList<Criterion>();

            // For all the subCriteria of the criterion check find its
            // preference according to the current profile and add it to the
            // appropriate list
            for (Criterion subCriterion : criterion.getSubCriteria()) {
                int preference;

                if (subCriterion.isComposite()) {
                    // If the subCriterion is composite, get it's pessimistic assignment and find its preference.
                    ComposedCriterion composedSubCriterion = (ComposedCriterion) subCriterion;
                    SQOOSSProfiles pessimistic = pessimisticAssignment(composedSubCriterion);
                    preference = composedSubCriterion.preference(pessimistic,
                            profiles[i]);
                } else {
                    // if its a NumericCriterionElement...just find its preference.
                    NumericCriterionElement subCriterionElement = (NumericCriterionElement) subCriterion;
                    preference = subCriterionElement.preference(profiles[i]);
                }
                // if the preference is -1.0 
                if (preference < 0)
                    GMinus.add(subCriterion);
                // if the preference is 1.0 
                else if (preference > 0)
                    GPlus.add(subCriterion);
                // if preference is 0.0 then it belongs to GEQUALS list which is not taken under consideration.
            }
            
            double relativeMinusWeightsSum = sumWeightList(GMinus);
            double relativePlusWeightsSum = sumWeightList(GPlus);
            
            // If the outranking succeeds then return the current profile.
            if (concordanceTestElement(relativeMinusWeightsSum,
                    relativePlusWeightsSum)
                    && !discordanceTest(relativeMinusWeightsSum))
                return profiles[i];
        }
        // in case of error
        return null;
    }
    /**
     * Assigns the ComposedCriterion to a profile in an optimistic way.<br>
     * It searches the {@link SQOOSSProfiles} from start to end until it finds a
     * profile that is definitely better than the <code>criterion</code> and returns the its previous.
     * 
     * @param criterion
     *            the {@link ComposedCriterion} to be assigned to a profile.
     * @return the previous profile  from the one that is definitely better than the
     *         <code>criterion</code>.If none was found returns null.<br>(<strong>Note:</strong>
     *         Using the default profiles is never gets to return null, since
     *         the worst profile has such values that any criterion will be
     *         always preferred)
     */
    static SQOOSSProfiles optimisticAssignment(ComposedCriterion criterion) {
        // Iteratively checks all profiles from start to end
        for (int i = 1; i < profiles.length; i++) {
         // The GMINUS list.
            ArrayList<Criterion> GMinus = new ArrayList<Criterion>();
            // The GPLUS list.
            ArrayList<Criterion> GPlus = new ArrayList<Criterion>();

            // For all the subCriteria of the criterion check find its
            // preference according to the current profile and add it to the
            // appropriate list
            for (Criterion subCriterion : criterion.getSubCriteria()) {
                int preference;

                if (subCriterion.isComposite()) {
                    // If the subCriterion is composite, get it's optimistic assignment and find its preference.
                    ComposedCriterion composedSubCriterion = (ComposedCriterion) subCriterion;
                    SQOOSSProfiles optimistic = optimisticAssignment(composedSubCriterion);
                    preference = composedSubCriterion.preference(optimistic,
                            profiles[i]);
                } else {
                    // if its a NumericCriterionElement...just find its preference.
                    NumericCriterionElement subCriterionElement = (NumericCriterionElement) subCriterion;
                    preference = subCriterionElement.preference(profiles[i]);
                }

             // if the preference is -1.0 
                if (preference < 0)
                    GMinus.add(subCriterion);
                // if the preference is 1.0 
                else if (preference > 0)
                    GPlus.add(subCriterion);
                // if preference is 0.0 then it belongs to GEQUALS list which is not taken under consideration.
            }

            double relativeMinusWeightsSum = sumWeightList(GMinus);
            double relativePlusWeightsSum = sumWeightList(GPlus);
            
            // If the outranking succeeds then return the current profile.
            if ((concordanceTestProfile(relativePlusWeightsSum,
                    relativeMinusWeightsSum) && !discordanceTest(relativePlusWeightsSum))
                    && !((concordanceTestElement(relativeMinusWeightsSum,
                            relativePlusWeightsSum) && !discordanceTest(relativeMinusWeightsSum))))
                return profiles[i - 1];
        }
        // TODO FIXME trouble
        return SQOOSSProfiles.Poor;
    }

    /**
     * Sums the {@link Criterion#getRelativeImportance()} af the
     * <code>list</code>.
     * 
     * @param list
     *            a {@link Criterion} list.
     * @return the sum of the relativeImportance of all the Criteria within the
     *         list.
     */
    private static double sumWeightList(List<Criterion> list) {
        double sum = 0.0d;
        for (Criterion decomposedCriterion : list)
            sum += decomposedCriterion.getRelativeImportance();
        return sum;
    }
}
