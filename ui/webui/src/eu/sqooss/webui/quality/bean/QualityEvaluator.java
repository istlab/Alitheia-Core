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

class QualityEvaluator {
	
	private static double concordanceThreshold;
	private static double discordanceThreshold;

	private static SQOOSSProfiles profiles[];

	static {
		profiles = SQOOSSProfiles.values();
		concordanceThreshold = 0.7d;
		discordanceThreshold = 0.28d;
	}

	private static boolean discordanceTest(double relativeMinusWeightsSum) {
		return relativeMinusWeightsSum > discordanceThreshold;
	}

	private static boolean concordanceTestElement(
			double relativeMinusWeightsSum, double relativePlusWeightsSum) {
		double relativeBothWeightsSum = relativeMinusWeightsSum
				+ relativePlusWeightsSum;

		return relativeBothWeightsSum >= concordanceThreshold
				&& relativePlusWeightsSum >= relativeMinusWeightsSum;
	}

	private static boolean concordanceTestProfile(
			double relativeMinusWeightsSum, double relativePlusWeightsSum) {
		return relativePlusWeightsSum > relativeMinusWeightsSum
				|| concordanceTestElement(relativeMinusWeightsSum,
						relativePlusWeightsSum);
	}

	static SQOOSSProfiles pessimisticAssignment(ComposedCriterion criterion){
		// Omitting the worst profile because it is surely better
		for (int i = profiles.length - 1; i > 0 ; i--) {
			ArrayList<Criterion> GMinus = new ArrayList<Criterion>();
			ArrayList<Criterion> GPlus = new ArrayList<Criterion>();
			
			for (Criterion subCriterion : criterion.getSubCriteria()) {
				int preference;
				
				if( subCriterion.isComposite()){
					ComposedCriterion composedSubCriterion = (ComposedCriterion)subCriterion;
					SQOOSSProfiles pessimistic = pessimisticAssignment(composedSubCriterion);
					preference = composedSubCriterion.preference(pessimistic,profiles[i]);
				}else
				{
					NumericCriterionElement subCriterionElement = (NumericCriterionElement) subCriterion;
					preference = subCriterionElement.preference(profiles[i]);
				}
				
				if(preference < 0)
					GMinus.add(subCriterion);
				else if (preference > 0)
					GPlus.add(subCriterion);
			}
			double relativeMinusWeightsSum = sumWeightList(GMinus);
			double relativePlusWeightsSum = sumWeightList(GPlus);
			if ( concordanceTestElement(relativeMinusWeightsSum,
					relativePlusWeightsSum)
					&& !discordanceTest(relativeMinusWeightsSum))
				return profiles[i];
		}
		// Return the worst profile that
		return profiles[0];
	}
	
	static SQOOSSProfiles optimisticAssignment(ComposedCriterion criterion){
		for (int i = 1; i< profiles.length  ; i++) {
			ArrayList<Criterion> GMinus = new ArrayList<Criterion>();
			ArrayList<Criterion> GPlus = new ArrayList<Criterion>();
			
			for (Criterion subCriterion : criterion.getSubCriteria()) {
				int preference;
				
				if( subCriterion.isComposite()){
					ComposedCriterion composedSubCriterion = (ComposedCriterion)subCriterion;
					SQOOSSProfiles optimistic = optimisticAssignment(composedSubCriterion);
					preference = composedSubCriterion.preference(optimistic,profiles[i]);
				}else
				{
					NumericCriterionElement subCriterionElement = (NumericCriterionElement) subCriterion;
					preference = subCriterionElement.preference(profiles[i]);
				}
				
				if(preference < 0)
					GMinus.add(subCriterion);
				else if (preference > 0)
					GPlus.add(subCriterion);
			}
			
			double relativeMinusWeightsSum = sumWeightList(GMinus);
			double relativePlusWeightsSum = sumWeightList(GPlus);
			if ( 
					(concordanceTestProfile(relativePlusWeightsSum,relativeMinusWeightsSum) &&
							!discordanceTest(relativePlusWeightsSum))
							&& !((concordanceTestElement(relativeMinusWeightsSum, relativePlusWeightsSum) && !discordanceTest(relativeMinusWeightsSum))))
				return profiles[i-1];
		}
		// TODO FIXME trouble
		return SQOOSSProfiles.Poor;
	}
	
	private static double sumWeightList(List<Criterion> list) {
		double sum = 0.0d;
		for (Criterion decomposedCriterion : list)
			sum += decomposedCriterion.getRelativeImportance();
		return sum;
	}
}
