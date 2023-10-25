package crypto.rules;

import java.util.List;

import crypto.interfaces.ICrySLPredicateParameter;
import crypto.interfaces.ISLConstraint;


/**
 * Extension of {@link CrySLPredicate}, to take the parameters into account,
 * when checking for Equality.
 */
public class ParameterAwarePredicate extends CrySLPredicate {

	public ParameterAwarePredicate(ICrySLPredicateParameter baseObject, String name, List<ICrySLPredicateParameter> variables, Boolean negated) {
		this(baseObject, name, variables, negated, null);
	}
	
	public ParameterAwarePredicate(ICrySLPredicateParameter baseObject, String name, List<ICrySLPredicateParameter> variables, Boolean negated, ISLConstraint constraint) {
		super(baseObject, name, variables, negated, constraint);
	}

	public CrySLPredicate toNormalPredicate() {
		return new CrySLPredicate(baseObject, predName, parameters, negated, constraint);
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof CrySLPredicate))
			return false;
		if (!super.equals(obj))
			return false;
		CrySLPredicate other = (CrySLPredicate) obj;
		return this.getParameters().equals(other.getParameters());
	}
}
