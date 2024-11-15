package crypto.rules;

import java.util.Arrays;

public class CrySLObject implements ICrySLPredicateParameter {

	private final String varName;
	private final String javaType;
	private final CrySLSplitter splitter;

	public CrySLObject(String name, String type) {
		this(name, type, null);
	}

	public CrySLObject(String name, String type, CrySLSplitter part) {
		varName = name;
		javaType = type;
		splitter = part;
	}

	/**
	 * @return the varName
	 */
	public String getVarName() {
		return varName;
	}

	/**
	 * @return the splitter
	 */
	public CrySLSplitter getSplitter() {
		return splitter;
	}

	@Override
	public boolean equals(Object other) {
		if (other == this)
			return true;
		if (other == null)
			return false;
		if (!(other instanceof CrySLObject))
			return false;
		CrySLObject object = (CrySLObject) other;
		return this.getJavaType().equals(object.getJavaType()) &&
				this.getName().equals(object.getName()) &&
				(this.getSplitter() == null || this.getSplitter().equals(object.getSplitter()));
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(new Object[] {
				varName,
				javaType,
				splitter
		});
	}

	@Override
	public String toString() {
		return javaType + " " + varName + ((splitter != null) ? splitter.toString() : "");
	}

	@Override
	public String getName() {
		return varName;
	}

	public String getJavaType() {
		return javaType;
	}

}
