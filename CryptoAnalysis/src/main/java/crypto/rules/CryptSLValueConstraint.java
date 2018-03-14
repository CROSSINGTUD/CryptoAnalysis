package crypto.rules;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CryptSLValueConstraint extends CryptSLLiteral implements java.io.Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	CryptSLObject var;
	List<String> valueRange;
	
	public CryptSLValueConstraint(CryptSLObject name, String value) { 
		var = name;
		valueRange = new ArrayList<String>();
		valueRange.add(value);
	}
	
	public CryptSLValueConstraint(CryptSLObject name, List<String> values) {
		var = name;
		valueRange = values;
	}
	
	/**
	 * @return the varName
	 */
	public String getVarName() {
		return var.getVarName();
	}
	
	/**
	 * @return the varName
	 */
	public CryptSLObject getVar() {
		return var;
	}
	
	
	/**
	 * @return the valueRange
	 */
	public List<String> getValueRange() {
		return valueRange;
	}

	public String toString() {
		StringBuilder vCSB = new StringBuilder();
		vCSB.append("VC:");
		vCSB.append(var);
		vCSB.append(" - ");
		for (String value : valueRange) {
			vCSB.append(value);
			vCSB.append(",");
		}
		return vCSB.toString();
	}

	@Override
	public Set<String> getInvolvedVarNames() {
		Set<String> varNames = new HashSet<String>();
		varNames.add(var.getVarName());
		return varNames;
	}

	@Override
	public String getName() {
		return toString();
	}
	
}
