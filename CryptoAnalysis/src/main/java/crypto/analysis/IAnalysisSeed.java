package crypto.analysis;

import boomerang.ForwardQuery;
import boomerang.jimple.AllocVal;
import boomerang.jimple.Statement;
import boomerang.jimple.Val;
import soot.SootMethod;

public abstract class IAnalysisSeed extends ForwardQuery{

	protected final CryptoScanner cryptoScanner;

	public IAnalysisSeed(CryptoScanner scanner, Statement stmt, Val fact){
		super(stmt,fact);
		this.cryptoScanner = scanner;
	}
	abstract void execute();

	abstract boolean contradictsNegations();
	
	public SootMethod getMethod(){
		return stmt().getMethod();
	}
	
}
