package crypto.rules;

import java.util.ArrayList;
import java.util.List;

import crypto.interfaces.ICryptSLPredicateParameter;
import crypto.interfaces.ISLConstraint;


public class ParEqualsPredicate extends CryptSLPredicate {

	public ParEqualsPredicate(ICryptSLPredicateParameter baseObject, String name, List<ICryptSLPredicateParameter> variables, Boolean not) {
		this(baseObject, name, variables, not, null);
	}
	
	public ParEqualsPredicate(ICryptSLPredicateParameter baseObject, String name, List<ICryptSLPredicateParameter> variables, Boolean not, ISLConstraint constraint) {
		super(baseObject, name, variables, not, constraint);
	}

	public CryptSLPredicate tobasicPredicate() {
		return new CryptSLPredicate(baseObject, predName, parameters, negated, optConstraint);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!super.equals(obj)) {
			return false;
		}
		CryptSLPredicate other = (CryptSLPredicate) obj;
		List<ICryptSLPredicateParameter> otherParams = new ArrayList<>(other.getParameters());
		if (otherParams.size() == parameters.size()) {
			otherParams.removeAll(parameters);
			return otherParams.isEmpty();
		}
		return false;
	}

	
}
