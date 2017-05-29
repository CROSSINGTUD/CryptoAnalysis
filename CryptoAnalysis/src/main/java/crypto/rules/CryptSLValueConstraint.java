package crypto.rules;

import java.util.ArrayList;
import java.util.List;

public class CryptSLValueConstraint extends CryptSLLiteral implements java.io.Serializable {
	
	String varName;
	List<String> valueRange;
	
	public CryptSLValueConstraint(String name, String value) { 
		varName = name;
		valueRange = new ArrayList<String>();
		valueRange.add(value);
	}
	
	public CryptSLValueConstraint(String name, List<String> values) {
		varName = name;
		valueRange = values;
	}
	
	/**
	 * @return the varName
	 */
	public String getVarName() {
		return varName;
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
		vCSB.append(varName);
		vCSB.append(" - ");
		for (String value : valueRange) {
			vCSB.append(value);
			vCSB.append(",");
		}
		return vCSB.toString();
	}
	
}
