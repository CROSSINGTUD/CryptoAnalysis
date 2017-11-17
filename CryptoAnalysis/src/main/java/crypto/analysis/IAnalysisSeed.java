package crypto.analysis;

import boomerang.jimple.Statement;
import boomerang.jimple.Val;
import crypto.typestate.CryptoTypestateAnaylsisProblem;
import soot.SootMethod;
import sync.pds.solver.nodes.Node;

public abstract class IAnalysisSeed extends Node<Statement,Val>{

	protected final CryptoScanner cryptoScanner;

	public IAnalysisSeed(CryptoScanner scanner, Statement stmt, Val fact){
		super(stmt,fact);
		this.cryptoScanner = scanner;
	}
	abstract void execute();

	abstract CryptoTypestateAnaylsisProblem getAnalysisProblem();

	abstract boolean contradictsNegations();
	
	public SootMethod getMethod(){
		return stmt.getMethod();
	}
	
}
