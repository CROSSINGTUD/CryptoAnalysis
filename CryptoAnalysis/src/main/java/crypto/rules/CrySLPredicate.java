package crypto.rules;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import crypto.interfaces.ICrySLPredicateParameter;
import crypto.interfaces.ISLConstraint;

public class CrySLPredicate extends CrySLLiteral implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	protected final ICrySLPredicateParameter baseObject;
	protected final String predName;
	protected final List<ICrySLPredicateParameter> parameters;
	protected final boolean negated;
	protected final ISLConstraint optConstraint;
	
	public CrySLPredicate(ICrySLPredicateParameter baseObject, String name, List<ICrySLPredicateParameter> variables, Boolean not) {
		this(baseObject, name, variables, not, null);
	}
	
	public CrySLPredicate(ICrySLPredicateParameter baseObject, String name, List<ICrySLPredicateParameter> variables, Boolean not, ISLConstraint constraint) {
		this.baseObject = baseObject;
		this.predName = name;
		this.parameters = variables;
		this.negated = not;
		this.optConstraint = constraint;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((predName == null) ? 0 : predName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof CrySLPredicate)) {
			return false;
		}
		CrySLPredicate other = (CrySLPredicate) obj;
		if (predName == null) {
			if (other.predName != null) {
				return false;
			}
		} else if (!predName.equals(other.predName)) {
			return false;
		}
		return true;
	}

	/**
	 * @return the baseObject
	 */
	public ICrySLPredicateParameter getBaseObject() {
		return baseObject;
	}

	/**
	 * @return the predName
	 */
	public String getPredName() {
		return predName;
	}
	
	/**
	 * @return the optConstraint
	 */
	public ISLConstraint getConstraint() {
		return optConstraint;
	}

	/**
	 * @return the parameters
	 */
	public List<ICrySLPredicateParameter> getParameters() {
		return parameters;
	}

	/**
	 * @return the negated
	 */
	public Boolean isNegated() {
		return negated;
	}
	
	public String toString() {
		StringBuilder predSB = new StringBuilder();
		if (negated) {
			predSB.append("!");
		}
		predSB.append(predName);
		predSB.append("(");
		
		for (ICrySLPredicateParameter parameter : parameters) {
			predSB.append(parameter);
			predSB.append(",");
		}
		predSB.reverse().deleteCharAt(0).reverse();
		predSB.append(")");
		
		
		return predSB.toString();
	}

	@Override
	public Set<String> getInvolvedVarNames() {
		Set<String> varNames = new HashSet<String>();
		if (Arrays.asList(new String[] {"neverTypeOf", "instanceOf"}).contains(predName)) {
			varNames.add(parameters.get(0).getName());
		} else {
		for (ICrySLPredicateParameter var : parameters) {
			if (!("_".equals(var.getName()) || "this".equals(var.getName()) || var instanceof CrySLMethod)) {
				varNames.add(var.getName());
			}
		}
		}
		if(getBaseObject() != null)
			varNames.add(getBaseObject().getName());
		return varNames;
	}
	
	public CrySLPredicate setNegated(boolean negated){
		if (negated == this.negated) {
			return this;
		} else {
			return new CrySLPredicate(baseObject, predName, parameters, negated);
		}
	}

	@Override
	public String getName() {
		if (parameters.size() == 1) {
			return parameters.get(0).getName();
		} else {
			return "";
		}
	}
}
