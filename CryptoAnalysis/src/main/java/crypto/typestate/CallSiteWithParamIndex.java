package crypto.typestate;

import boomerang.accessgraph.AccessGraph;
import ideal.FactAtStatement;
import soot.jimple.Stmt;

public class CallSiteWithParamIndex extends FactAtStatement{

	private String varName;
	
	/**
	 * @return the varName
	 */
	public String getVarName() {
		return varName;
	}

	private int index;

	public CallSiteWithParamIndex(Stmt u, AccessGraph fact, int index, String varName) {
		super(u, fact);
		this.index = index;
		this.varName = varName;
	}

	public int getIndex() {
		return index;
	}
}
