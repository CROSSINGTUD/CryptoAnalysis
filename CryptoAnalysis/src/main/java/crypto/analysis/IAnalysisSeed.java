package crypto.analysis;

import boomerang.accessgraph.AccessGraph;
import crypto.typestate.CryptoTypestateAnaylsisProblem;
import ideal.IFactAtStatement;
import soot.SootMethod;
import soot.Unit;

public abstract class IAnalysisSeed implements IFactAtStatement{

	final protected CryptoScanner cryptoScanner;
	final protected IFactAtStatement factAtStmt;

	public IAnalysisSeed(CryptoScanner cryptoScanner, IFactAtStatement factAtStmt){
		this.cryptoScanner = cryptoScanner;
		this.factAtStmt = factAtStmt;
	}
	abstract void execute();

	abstract CryptoTypestateAnaylsisProblem getAnalysisProblem();

	abstract boolean isSolved();

	abstract boolean contradictsNegations();
	
	public SootMethod getMethod(){
		return cryptoScanner.icfg().getMethodOf(getStmt());
	}
	
	@Override
	public AccessGraph getFact() {
		return factAtStmt.getFact();
	}
	
	@Override
	public Unit getStmt() {
		return factAtStmt.getStmt();
	}
}
