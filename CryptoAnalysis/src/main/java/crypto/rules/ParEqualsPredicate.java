package crypto.rules;

import java.util.ArrayList;
import java.util.List;

import crypto.interfaces.ICrySLPredicateParameter;
import crypto.interfaces.ISLConstraint;


public class ParEqualsPredicate extends CrySLPredicate {

	public ParEqualsPredicate(ICrySLPredicateParameter baseObject, String name, List<ICrySLPredicateParameter> variables, Boolean not) {
		this(baseObject, name, variables, not, null);
	}
	
	public ParEqualsPredicate(ICrySLPredicateParameter baseObject, String name, List<ICrySLPredicateParameter> variables, Boolean not, ISLConstraint constraint) {
		super(baseObject, name, variables, not, constraint);
	}

	public CrySLPredicate tobasicPredicate() {
		return new CrySLPredicate(baseObject, predName, parameters, negated, optConstraint);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!super.equals(obj)) {
			return false;
		}
		CrySLPredicate other = (CrySLPredicate) obj;
		List<ICrySLPredicateParameter> otherParams = new ArrayList<>(other.getParameters());
		if (otherParams.size() == parameters.size()) {
			otherParams.removeAll(parameters);
			return otherParams.isEmpty();
		}
		return false;
	}

	
}
