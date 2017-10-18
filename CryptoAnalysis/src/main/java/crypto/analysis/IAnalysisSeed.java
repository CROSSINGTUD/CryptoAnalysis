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
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((factAtStmt == null) ? 0 : factAtStmt.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		IAnalysisSeed other = (IAnalysisSeed) obj;
		if (factAtStmt == null) {
			if (other.factAtStmt != null)
				return false;
		} else if (!factAtStmt.equals(other.factAtStmt))
			return false;
		return true;
	}
	
}
