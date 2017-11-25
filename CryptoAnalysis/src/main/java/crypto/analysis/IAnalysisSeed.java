package crypto.analysis;

import boomerang.WeightedForwardQuery;
import boomerang.jimple.Statement;
import boomerang.jimple.Val;
import soot.SootMethod;
import typestate.TransitionFunction;

public abstract class IAnalysisSeed extends WeightedForwardQuery<TransitionFunction> {

	protected final CryptoScanner cryptoScanner;

	public IAnalysisSeed(CryptoScanner scanner, Statement stmt, Val fact, TransitionFunction func){
		super(stmt,fact, func);
		this.cryptoScanner = scanner;
	}
	abstract void execute();

	abstract boolean contradictsNegations();
	
	public SootMethod getMethod(){
		return stmt().getMethod();
	}
	
}
