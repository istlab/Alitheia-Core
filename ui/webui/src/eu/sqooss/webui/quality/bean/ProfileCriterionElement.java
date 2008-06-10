/**
 * 
 */
package eu.sqooss.webui.quality.bean;

import java.util.Arrays;
import java.util.List;

/**
 * @author sskalist
 * 
 */
public class ProfileCriterionElement extends Criterion {

	private SQOOSSProfiles profileValue;

	private List<SQOOSSProfiles> profilesValues;

	public ProfileCriterionElement(String name, double relativeImportance,
			SQOOSSProfiles profileValue, CriterionScale criterionScale,
			List<SQOOSSProfiles> profilesValues) {
		super(name, relativeImportance,criterionScale);
		initialize(relativeImportance,profileValue,profilesValues);
		
		
	}

	public ProfileCriterionElement(String name, double relativeImportance,
			SQOOSSProfiles profileValue, CriterionScale criterionScale) {
		super(name, relativeImportance,criterionScale);
		initialize(relativeImportance, profileValue, Arrays.asList(SQOOSSProfiles.values()));		
	}
	
	public ProfileCriterionElement(String name, double relativeImportance,
			SQOOSSProfiles profileValue) {
		super(name, relativeImportance,CriterionScale.MoreIsBetter);
		initialize(relativeImportance, profileValue, Arrays.asList(SQOOSSProfiles.values()));		
	}
	
	private void initialize(double relativeImportance, SQOOSSProfiles profileValue, List<SQOOSSProfiles> profilesValues)
	{
		this.profileValue = profileValue;
		if (profilesValues.size() != SQOOSSProfiles.getNumberOfProfiles())
			throw new RuntimeException("Invalid Number of Profile Values");
		// TODO check scale within profile values!
		this.profilesValues = profilesValues;
		
	}
	
	public int preference(SQOOSSProfiles profile) {
		return compare(this.profileValue, profilesValues.get(profile.ordinal()), getCriterionScale());
	}

	@Override
	public boolean isComposite() {
		return false;
	}

}
