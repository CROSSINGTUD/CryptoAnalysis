package crypto.rules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CrySLPredicate extends CrySLLiteral {

	protected final ICrySLPredicateParameter baseObject;
	protected final String predName;
	protected final List<ICrySLPredicateParameter> parameters;
	protected final boolean negated;
	protected final Optional<ISLConstraint> constraint;
	
	public CrySLPredicate(ICrySLPredicateParameter baseObject, String name, List<ICrySLPredicateParameter> parameters, Boolean negated) {
		this(baseObject, name, parameters, negated, Optional.empty());
	}
	
	public CrySLPredicate(ICrySLPredicateParameter baseObject, String name, List<ICrySLPredicateParameter> parameters, Boolean negated, ISLConstraint constraint) {
		this(baseObject, name, parameters, negated, Optional.ofNullable(constraint));
	}

	public CrySLPredicate(ICrySLPredicateParameter baseObject, String name, List<ICrySLPredicateParameter> parameters, Boolean negated, Optional<ISLConstraint> constraint) {
		this.baseObject = baseObject;
		this.predName = name;
		this.parameters = parameters;
		this.negated = negated;
		this.constraint = constraint;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((predName == null) ? 0 : predName.hashCode());
		result = prime * result + this.getConstraint().hashCode();
		result = prime * result + this.getParameters().hashCode();
		return result;
	
	}

	// TODO Make comparison with parameters here
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (!(obj instanceof CrySLPredicate)) {
			return false;
		}

		CrySLPredicate other = (CrySLPredicate) obj;
		if (!getPredName().equals(other.getPredName())) {
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
	public Optional<ISLConstraint> getConstraint() {
		return this.constraint;
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
		if (negated)
			predSB.append("!");
		predSB.append(predName);
		predSB.append("(");
		predSB.append(parameters.stream().map(x -> x.toString()).collect(Collectors.joining(", ")));
		predSB.append(")");
		
		
		return predSB.toString();
	}

	@Override
	public List<String> getInvolvedVarNames() {
		List<String> varNames = new ArrayList<>();
		if (Arrays.asList(new String[] {"neverTypeOf", "instanceOf"}).contains(predName)) {
			varNames.add(parameters.get(0).getName());
		} else {
			for (ICrySLPredicateParameter var : parameters) {
				if (!("_".equals(var.getName()) || "this".equals(var.getName()) || var instanceof CrySLMethod)) {
					varNames.add(var.getName());
				}
			}
		}
		if (getBaseObject() != null)
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
