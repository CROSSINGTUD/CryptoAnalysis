package crypto.analysis;

import java.util.List;

import boomerang.jimple.Statement;
import crypto.rules.CryptSLPredicate;
import crypto.rules.ParEqualsPredicate;
import typestate.interfaces.ICryptSLPredicateParameter;
import typestate.interfaces.ISLConstraint;

public class LocatedCrySLPredicate extends ParEqualsPredicate {

	private static final long serialVersionUID = 7788683885015439800L;
	private final Statement location;
	
	public LocatedCrySLPredicate(CryptSLPredicate pred, Statement loc) {
		this(pred.getBaseObject(), pred.getPredName(), pred.getParameters(), pred.isNegated(), pred.getConstraint(), loc);
	}
	
	public LocatedCrySLPredicate(ICryptSLPredicateParameter baseObject, String name,
			List<ICryptSLPredicateParameter> variables, Boolean not, ISLConstraint constraint, Statement loc) {
		super(baseObject, name, variables, not, constraint);
		this.location = loc;
	}

	public Statement getLocation() {
		return location;
	}
	
}
