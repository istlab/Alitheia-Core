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
