package crypto.analysis.errors;

import boomerang.scene.Statement;
import crypto.analysis.HiddenPredicate;
import crypto.analysis.IAnalysisSeed;
import crypto.extractparameter.CallSiteWithExtractedValue;
import crypto.rules.CrySLPredicate;
import crypto.rules.CrySLRule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>Creates {@link RequiredPredicateError} for all Required Predicate error generates RequiredPredicateError</p>
 */
public class RequiredPredicateError extends AbstractError {

	private final List<CrySLPredicate> contradictedPredicate;
	private final CallSiteWithExtractedValue extractedValues;
	private final List<HiddenPredicate> hiddenPredicates;

	public RequiredPredicateError(IAnalysisSeed seed, Statement errorStmt, CrySLRule rule, CallSiteWithExtractedValue cs, List<CrySLPredicate> contradictedPredicates) {
		super(seed, errorStmt, rule);

		this.contradictedPredicate = contradictedPredicates;
		this.extractedValues = cs;
		this.hiddenPredicates = new ArrayList<>();
	}

	public void addHiddenPredicates(Collection<HiddenPredicate> hiddenPredicates) {
		this.hiddenPredicates.addAll(hiddenPredicates);
	}

	public void mapPrecedingErrors() {
		for (HiddenPredicate hiddenPredicate : hiddenPredicates) {
			Collection<AbstractError> precedingErrors = hiddenPredicate.getPrecedingErrors();
			this.addCausingError(precedingErrors);
			precedingErrors.forEach(e -> e.addSubsequentError(this));
		}
	}

	/**
	 * This method returns a list of contradicting predicates
	 * @return list of contradicting predicates
	 */
	public List<CrySLPredicate> getContradictedPredicates() {
		return contradictedPredicate;
	}
	
	public CallSiteWithExtractedValue getExtractedValues() {
		return extractedValues;
	}

	public List<HiddenPredicate> getHiddenPredicates() {
		return hiddenPredicates;
	}

	@Override
	public String toErrorMarkerString() {
		StringBuilder msg = new StringBuilder(extractedValues.toString());
		msg.append(" was not properly generated as ");
		String predicateName = getContradictedPredicates().stream().map(CrySLPredicate::getPredName).collect(Collectors.joining(" OR "));
		String[] parts = predicateName.split("(?=[A-Z])");
		msg.append(parts[0]);
		for (int i = 1; i < parts.length; i++) {
			msg.append(parts[i]);
		}

		if (predicateName.equals("preparedIV") && extractedValues.toString().equals("Third parameter")) {
			msg.append(" [ with CBC, It's required to use IVParameterSpec]");
		}
		return msg.toString();
	}
	
	@Override
	public int hashCode() {
		return Arrays.hashCode(new Object[]{
				super.hashCode(),
				contradictedPredicate,
				extractedValues,
				hiddenPredicates
		});
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!super.equals(obj)) return false;
		if (getClass() != obj.getClass()) return false;

		RequiredPredicateError other = (RequiredPredicateError) obj;
		if (contradictedPredicate == null) {
			if (other.getContradictedPredicates() != null) return false;
		} else if (!contradictedPredicate.equals(other.getContradictedPredicates())) {
			return false;
		}

		if (extractedValues == null) {
			if (other.getExtractedValues() != null) return false;
		} else if (!extractedValues.equals(other.getExtractedValues())) {
			return false;
		}

		if (hiddenPredicates == null) {
			if (other.getHiddenPredicates() != null) return false;
		} else if (!hiddenPredicates.equals(other.getHiddenPredicates())) {
			return false;
		}

		return true;
	}

	@Override
	public String toString() {
		return "RequiredPredicateError: " + toErrorMarkerString();
	}

}
