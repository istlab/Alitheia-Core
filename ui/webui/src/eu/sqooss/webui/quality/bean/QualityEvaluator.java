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
